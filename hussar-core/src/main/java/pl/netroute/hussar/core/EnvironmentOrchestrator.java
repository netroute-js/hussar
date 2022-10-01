package pl.netroute.hussar.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.netroute.hussar.core.api.Application;
import pl.netroute.hussar.core.api.EnvironmentConfigurerProvider;

import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class EnvironmentOrchestrator {
    private static final Logger LOG = LoggerFactory.getLogger(EnvironmentOrchestrator.class);
    private static final Duration TRY_LOCK_TIMEOUT = Duration.ofSeconds(10L);

    private final PropertiesConfigurer propertiesConfigurer;
    private final PropertiesCleaner propertiesCleaner;
    private final ServicesStarter servicesStarter;
    private final ServicesStopper servicesStopper;

    private final ReentrantReadWriteLock lock;
    private final Map<EnvironmentConfigurerProvider, Environment> initializedEnvironments;

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
        this.lock = new ReentrantReadWriteLock();
        this.initializedEnvironments = new ConcurrentHashMap<>();
    }

    void initialize(EnvironmentConfigurerProvider environmentConfigurerProvider) {
        Objects.requireNonNull(environmentConfigurerProvider, "environmentConfigurerProvider is required");

        sharedLockedAction(() -> initializedEnvironments.computeIfAbsent(environmentConfigurerProvider, this::initializeEnvironment));
    }

    void shutdown() {
        LOG.info("Shutting down all environments");

        exclusiveLockedAction(() -> {
            initializedEnvironments
                    .entrySet()
                    .stream()
                    .map(Map.Entry::getValue)
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
        application.start();
    }

    private void shutdownApplication(Application application) {
        application.shutdown();
    }

    private void exclusiveLockedAction(Runnable action) {
        lockedAction(action, true);
    }

    private void sharedLockedAction(Runnable action) {
        lockedAction(action, false);
    }

    private void lockedAction(Runnable action, boolean isWriteLock) {
        var acquiredLock = isWriteLock ? lock.writeLock() : lock.readLock();

        try {
            acquiredLock.tryLock(TRY_LOCK_TIMEOUT.toMillis(), TimeUnit.MILLISECONDS);

            action.run();
        } catch (InterruptedException ex) {
            throw new IllegalStateException("Could not acquire lock", ex);
        } finally {
            acquiredLock.unlock();
        }
    }

}
