package pl.netroute.hussar.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.netroute.hussar.core.api.Application;
import pl.netroute.hussar.core.api.ApplicationStartupContext;
import pl.netroute.hussar.core.api.EnvironmentConfigurerProvider;
import pl.netroute.hussar.core.lock.LockedAction;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

class EnvironmentOrchestrator {
    private static final Logger LOG = LoggerFactory.getLogger(EnvironmentOrchestrator.class);

    private final PropertiesConfigurer propertiesConfigurer;
    private final PropertiesCleaner propertiesCleaner;
    private final ServicesStarter servicesStarter;
    private final ServicesStopper servicesStopper;

    private final LockedAction lockedAction;
    private final Map<Class<? extends EnvironmentConfigurerProvider>, Environment> initializedEnvironments;

    EnvironmentOrchestrator(PropertiesConfigurer propertiesConfigurer,
                            PropertiesCleaner propertiesCleaner,
                            ServicesStarter servicesStarter,
                            ServicesStopper servicesStopper) {
        Objects.requireNonNull(propertiesConfigurer, "propertiesInjector is required");
        Objects.requireNonNull(propertiesCleaner, "propertiesCleaner is required");
        Objects.requireNonNull(servicesStarter, "servicesStarter is required");
        Objects.requireNonNull(servicesStopper, "servicesStopper is required");

        this.propertiesConfigurer = propertiesConfigurer;
        this.propertiesCleaner = propertiesCleaner;
        this.servicesStarter = servicesStarter;
        this.servicesStopper = servicesStopper;
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

        var properties = environment.getPropertiesConfiguration();
        var services = environment.getServicesConfiguration();
        var application = environment.getApplication();

        configureProperties(properties);
        startServices(services);
        startApplication(application);

        return environment;
    }

    private void shutdownEnvironment(Environment setup) {
        var properties = setup.getPropertiesConfiguration();
        var services = setup.getServicesConfiguration();
        var application = setup.getApplication();

        shutdownApplication(application);
        shutdownServices(services);
        clearProperties(properties);
    }

    private void configureProperties(PropertiesConfiguration propertiesConfig) {
        propertiesConfigurer.configure(propertiesConfig);
    }

    private void clearProperties(PropertiesConfiguration propertiesConfig) {
        propertiesCleaner.clean(propertiesConfig);
    }

    private void startServices(ServicesConfiguration servicesConfig) {
        servicesStarter.start(servicesConfig);
    }

    private void shutdownServices(ServicesConfiguration servicesConfig) {
        servicesStopper.stop(servicesConfig);
    }

    private void startApplication(Application application) {
        var startupContext = new ApplicationStartupContext(Map.of());

        application.start(startupContext);
    }

    private void shutdownApplication(Application application) {
        application.shutdown();
    }

}
