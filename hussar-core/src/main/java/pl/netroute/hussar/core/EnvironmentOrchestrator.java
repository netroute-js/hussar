package pl.netroute.hussar.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.netroute.hussar.core.api.Application;
import pl.netroute.hussar.core.api.ApplicationStartupContext;
import pl.netroute.hussar.core.api.EnvironmentConfigurerProvider;
import pl.netroute.hussar.core.api.ServiceRegistry;
import pl.netroute.hussar.core.lock.LockedAction;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

class EnvironmentOrchestrator {
    private static final Logger LOG = LoggerFactory.getLogger(EnvironmentOrchestrator.class);

    private final ServiceStarter serviceStarter;
    private final ServiceStopper serviceStopper;

    private final LockedAction lockedAction;
    private final Map<Class<? extends EnvironmentConfigurerProvider>, Environment> initializedEnvironments;

    EnvironmentOrchestrator(ServiceStarter serviceStarter,
                            ServiceStopper serviceStopper) {
        Objects.requireNonNull(serviceStarter, "servicesStarter is required");
        Objects.requireNonNull(serviceStopper, "servicesStopper is required");

        this.serviceStarter = serviceStarter;
        this.serviceStopper = serviceStopper;
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

        var services = environment.getServiceRegistry();
        var application = environment.getApplication();

        startServices(services);
        startApplication(application);

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

    private void startApplication(Application application) {
        var startupContext = new ApplicationStartupContext(Map.of());

        application.start(startupContext);
    }

    private void shutdownApplication(Application application) {
        application.shutdown();
    }

}
