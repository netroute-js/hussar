package pl.netroute.hussar.core.environment;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import pl.netroute.hussar.core.api.InternalUseOnly;
import pl.netroute.hussar.core.application.ApplicationStartupContext;
import pl.netroute.hussar.core.application.api.Application;
import pl.netroute.hussar.core.configuration.api.ConfigurationEntry;
import pl.netroute.hussar.core.configuration.api.ConfigurationRegistry;
import pl.netroute.hussar.core.environment.api.Environment;
import pl.netroute.hussar.core.helper.CollectionHelper;
import pl.netroute.hussar.core.network.NetworkOperatorStartupContext;
import pl.netroute.hussar.core.network.api.NetworkOperator;
import pl.netroute.hussar.core.service.api.Service;
import pl.netroute.hussar.core.service.api.ServiceRegistry;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

/**
 * An actual implementation of {@link Environment}.
 */
@Slf4j
@InternalUseOnly
@RequiredArgsConstructor
public class LocalEnvironment implements Environment {

    @Getter
    @NonNull
    private final Application application;

    @Getter
    @NonNull
    private final ConfigurationRegistry configurationRegistry;

    @Getter
    @NonNull
    private final ServiceRegistry serviceRegistry;

    @NonNull
    private final NetworkOperator networkOperator;

    @Override
    public void start(@NotNull EnvironmentStartupContext context) {
        startNetworkOperator();
        startServices();
        startApplication();
    }

    @Override
    public void shutdown() {
        shutdownApplication();
        shutdownServices();
        shutdownNetworkOperator();
    }

    private void startApplication() {
        var externalConfigurations = extractExternalConfiguration(configurationRegistry, serviceRegistry);
        var startupContext = new ApplicationStartupContext(externalConfigurations);

        log.info("Starting application {}. Configuration {}", application.getClass().getCanonicalName(), startupContext.externalConfigurations());
        application.start(startupContext);
    }

    private void shutdownApplication() {
        application.shutdown();
    }

    private void startNetworkOperator() {
        var context = new NetworkOperatorStartupContext();

        networkOperator.start(context);
    }

    private void startServices() {
        var serviceStarter = new ServiceStarter(ForkJoinPool.commonPool());

        serviceStarter.start(serviceRegistry);
    }

    private void shutdownServices() {
        var serviceStopper = new ServiceStopper(ForkJoinPool.commonPool());

        serviceStopper.stop(serviceRegistry);
    }

    private void shutdownNetworkOperator() {
        networkOperator.shutdown();
    }

    private Set<ConfigurationEntry> extractExternalConfiguration(ConfigurationRegistry configurationRegistry,
                                                                 ServiceRegistry serviceRegistry) {
        var staticConfigurations = configurationRegistry.getEntries();

        var servicesConfigurations = serviceRegistry
                .getEntries()
                .stream()
                .map(Service::getConfigurationRegistry)
                .map(ConfigurationRegistry::getEntries)
                .flatMap(Collection::stream)
                .collect(Collectors.toUnmodifiableSet());

        return CollectionHelper.mergeSets(staticConfigurations, servicesConfigurations);
    }

}
