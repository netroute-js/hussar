package pl.netroute.hussar.core;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.netroute.hussar.core.api.Application;
import pl.netroute.hussar.core.api.ApplicationStartupContext;
import pl.netroute.hussar.core.api.ConfigurationEntry;
import pl.netroute.hussar.core.api.ConfigurationRegistry;
import pl.netroute.hussar.core.api.Environment;
import pl.netroute.hussar.core.api.EnvironmentConfigurerProvider;
import pl.netroute.hussar.core.api.Service;
import pl.netroute.hussar.core.api.ServiceRegistry;
import pl.netroute.hussar.core.helper.CollectionHelper;
import pl.netroute.hussar.core.lock.LockedAction;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class EnvironmentOrchestrator {

    @NonNull
    private final ServiceStarter serviceStarter;

    @NonNull
    private final ServiceStopper serviceStopper;

    private final LockedAction lockedAction = new LockedAction();
    private final Map<Class<? extends EnvironmentConfigurerProvider>, Environment> initializedEnvironments = new ConcurrentHashMap<>();

    Environment initialize(@NonNull EnvironmentConfigurerProvider environmentConfigurerProvider) {
        var configurerType = environmentConfigurerProvider.getClass();

        return lockedAction.sharedAction(() -> initializedEnvironments.computeIfAbsent(configurerType, actualConfigurerType -> initializeEnvironment(environmentConfigurerProvider)));
    }

    void shutdown() {
        log.info("Shutting down all environments");

        lockedAction.exclusiveAction(() -> {
            initializedEnvironments
                    .values()
                    .forEach(this::shutdownEnvironment);

            initializedEnvironments.clear();
        });
    }

    private Environment initializeEnvironment(EnvironmentConfigurerProvider provider) {
        log.info("Initializing environment for {}", provider.getClass());

        var environment = provider
                .provide()
                .configure();

        var serviceRegistry = environment.serviceRegistry();
        var application = environment.application();

        startServices(serviceRegistry);
        startApplication(application, environment);

        return environment;
    }

    private void shutdownEnvironment(Environment setup) {
        var services = setup.serviceRegistry();
        var application = setup.application();

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
        var externalConfigurations = extractExternalConfiguration(environment);
        var startupContext = new ApplicationStartupContext(externalConfigurations);

        log.info("Starting application {}. Configuration {}", application.getClass().getCanonicalName(), startupContext.externalConfigurations());
        application.start(startupContext);
    }

    private Set<ConfigurationEntry> extractExternalConfiguration(Environment environment) {
        var staticConfigurations = environment
                .configurationRegistry()
                .getEntries();

        var servicesConfigurations = environment
                .serviceRegistry()
                .getEntries()
                .stream()
                .map(Service::getConfigurationRegistry)
                .filter(Objects::nonNull)
                .map(ConfigurationRegistry::getEntries)
                .flatMap(Collection::stream)
                .collect(Collectors.toUnmodifiableSet());

        return CollectionHelper.mergeSets(staticConfigurations, servicesConfigurations);
    }

    private void shutdownApplication(Application application) {
        application.shutdown();
    }

}
