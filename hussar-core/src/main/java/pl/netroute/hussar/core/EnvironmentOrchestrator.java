package pl.netroute.hussar.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.netroute.hussar.core.api.Application;
import pl.netroute.hussar.core.api.ApplicationStartupContext;
import pl.netroute.hussar.core.api.ConfigurationRegistry;
import pl.netroute.hussar.core.api.EnvironmentConfigurerProvider;
import pl.netroute.hussar.core.api.Service;
import pl.netroute.hussar.core.api.ServiceRegistry;
import pl.netroute.hussar.core.helper.CollectionHelper;
import pl.netroute.hussar.core.lock.LockedAction;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

class EnvironmentOrchestrator {
    private static final Logger LOG = LoggerFactory.getLogger(EnvironmentOrchestrator.class);

    private final ServiceStarter serviceStarter;
    private final ServiceStopper serviceStopper;
    private final ApplicationConfigurationResolver applicationConfigurationResolver;

    private final LockedAction lockedAction;
    private final Map<Class<? extends EnvironmentConfigurerProvider>, Environment> initializedEnvironments;

    EnvironmentOrchestrator(ServiceStarter serviceStarter,
                            ServiceStopper serviceStopper,
                            ApplicationConfigurationResolver applicationConfigurationResolver) {
        Objects.requireNonNull(serviceStarter, "servicesStarter is required");
        Objects.requireNonNull(serviceStopper, "servicesStopper is required");
        Objects.requireNonNull(serviceStopper, "applicationConfigurationResolver is required");

        this.serviceStarter = serviceStarter;
        this.serviceStopper = serviceStopper;
        this.applicationConfigurationResolver = applicationConfigurationResolver;
        this.lockedAction = new LockedAction();
        this.initializedEnvironments = new ConcurrentHashMap<>();
    }

    Environment initialize(EnvironmentConfigurerProvider environmentConfigurerProvider) {
        Objects.requireNonNull(environmentConfigurerProvider, "environmentConfigurerProvider is required");

        var configurerType = environmentConfigurerProvider.getClass();

        return lockedAction.sharedAction(() -> initializedEnvironments.computeIfAbsent(configurerType, actualConfigurerType -> initializeEnvironment(environmentConfigurerProvider)));
    }

    void shutdown() {
        LOG.info("Shutting down all environments");

        lockedAction.exclusiveAction(() -> {
            initializedEnvironments
                    .values()
                    .forEach(this::shutdownEnvironment);

            initializedEnvironments.clear();
        });
    }

    private Environment initializeEnvironment(EnvironmentConfigurerProvider provider) {
        LOG.info("Initializing environment for {}", provider.getClass());

        var environment = provider
                .provide()
                .configure();

        var serviceRegistry = environment.getServiceRegistry();
        var application = environment.getApplication();

        startServices(serviceRegistry);
        startApplication(application, environment);

        return environment;
    }

    private void shutdownEnvironment(Environment setup) {
        var services = setup.getServiceRegistry();
        var application = setup.getApplication();

        shutdownApplication(application);
        shutdownServices(services);
    }

    private void startServices(ServiceRegistry serviceRegistry) {
        serviceStarter.start(serviceRegistry);
    }

    private void shutdownServices(ServiceRegistry serviceRegistry) {
        serviceStopper.stop(serviceRegistry);
    }

    private void startApplication(Application application, Environment environment) {
        var externalConfiguration = extractExternalConfiguration(environment);
        var applicationConfiguration = applicationConfigurationResolver.resolve(application, externalConfiguration);
        var startupContext = new ApplicationStartupContext(applicationConfiguration);

        LOG.info("Starting application {}. Configuration {}", application.getClass().getCanonicalName(), startupContext.getProperties());
        application.start(startupContext);
    }

    private List<ConfigurationRegistry> extractExternalConfiguration(Environment environment) {
        var staticConfiguration = List.of(environment.getStaticConfigurationRegistry());

        var servicesConfiguration = environment
                .getServiceRegistry()
                .getEntries()
                .stream()
                .map(Service::getConfigurationRegistry)
                .filter(Objects::nonNull)
                .collect(Collectors.toUnmodifiableList());

        return CollectionHelper.mergeLists(staticConfiguration, servicesConfiguration);
    }

    private void shutdownApplication(Application application) {
        application.shutdown();
    }

}
