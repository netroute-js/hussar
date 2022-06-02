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

    private final ReentrantReadWriteLock lock;
    private final Map<EnvironmentConfigurerProvider, Environment> initializedEnvironments;

    EnvironmentOrchestrator(PropertiesConfigurer propertiesConfigurer,
                            PropertiesCleaner propertiesCleaner) {
        Objects.requireNonNull(propertiesConfigurer, "propertiesInjector is required");
        Objects.requireNonNull(propertiesCleaner, "propertiesCleaner is required");

        this.propertiesConfigurer = propertiesConfigurer;
        this.propertiesCleaner = propertiesCleaner;
        this.lock = new ReentrantReadWriteLock();
        this.initializedEnvironments = new ConcurrentHashMap<>();
    }

    void initialize(EnvironmentConfigurerProvider environmentConfigurerProvider) {
        Objects.requireNonNull(environmentConfigurerProvider, "environmentConfigurerProvider is required");

        sharedLockedAction(() -> initializedEnvironments.computeIfAbsent(environmentConfigurerProvider, this::initializeEnvironment));
    }

    void shutdown() {
        LOG.debug("Shutting down all environments");

        exclusiveLockedAction(() -> {
            initializedEnvironments
                    .entrySet()
                    .stream()
                    .map(Map.Entry::getValue)
                    .forEach(this::shutdownEnvironment);
        });
    }

    private Environment initializeEnvironment(EnvironmentConfigurerProvider provider) {
        var environment = provider
                .provide()
                .configure();

        var properties = environment.getPropertiesConfiguration();
        var mocks = environment.getMocksConfiguration();
        var application = environment.getApplicationProvider();

        configureProperties(properties);
        startMocks(mocks);
        startApplication(application);

        return environment;
    }

    private void shutdownEnvironment(Environment setup) {
        var properties = setup.getPropertiesConfiguration();
        var mocks = setup.getMocksConfiguration();
        var application = setup.getApplicationProvider();

        shutdownApplication(application);
        shutdownMocks(mocks);
        clearProperties(properties);
    }

    private void configureProperties(PropertiesConfiguration properties) {
        propertiesConfigurer.configure(properties);
    }

    private void clearProperties(PropertiesConfiguration properties) {
        propertiesCleaner.clean(properties);
    }

    private void startMocks(MocksConfiguration mocks) {
    }

    private void shutdownMocks(MocksConfiguration mocks) {
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
