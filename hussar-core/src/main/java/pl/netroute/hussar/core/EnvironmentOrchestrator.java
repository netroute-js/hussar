package pl.netroute.hussar.core;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.netroute.hussar.core.api.Application;
import pl.netroute.hussar.core.api.ApplicationStartupContext;
import pl.netroute.hussar.core.api.ConfigurationRegistry;
import pl.netroute.hussar.core.api.Environment;
import pl.netroute.hussar.core.api.EnvironmentConfigurerProvider;
import pl.netroute.hussar.core.api.Service;
import pl.netroute.hussar.core.api.ServiceRegistry;
import pl.netroute.hussar.core.helper.CollectionHelper;
import pl.netroute.hussar.core.lock.LockedAction;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class EnvironmentOrchestrator {

    @NonNull
    private final ServiceStarter serviceStarter;

    @NonNull
    private final ServiceStopper serviceStopper;

    @NonNull
    private final ApplicationConfigurationResolver applicationConfigurationResolver;

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
        var externalConfiguration = extractExternalConfiguration(environment);
        var applicationConfiguration = applicationConfigurationResolver.resolve(application, externalConfiguration);
        var startupContext = new ApplicationStartupContext(applicationConfiguration);

        log.info("Starting application {}. Configuration {}", application.getClass().getCanonicalName(), startupContext.properties());
        application.start(startupContext);
    }

    private List<ConfigurationRegistry> extractExternalConfiguration(Environment environment) {
        var staticConfiguration = List.of(environment.configurationRegistry());

        var servicesConfiguration = environment
                .serviceRegistry()
                .getEntries()
                .stream()
                .map(Service::getConfigurationRegistry)
                .filter(Objects::nonNull)
                .toList();

        return CollectionHelper.mergeLists(staticConfiguration, servicesConfiguration);
    }

    private void shutdownApplication(Application application) {
        application.shutdown();
    }

}
