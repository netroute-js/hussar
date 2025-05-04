package pl.netroute.hussar.core.environment;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import pl.netroute.hussar.core.application.ApplicationStartupContext;
import pl.netroute.hussar.core.configuration.api.ConfigurationEntry;
import pl.netroute.hussar.core.configuration.api.ConfigurationRegistry;
import pl.netroute.hussar.core.environment.api.Environment;
import pl.netroute.hussar.core.helper.CollectionHelper;
import pl.netroute.hussar.core.network.api.NetworkOperator;
import pl.netroute.hussar.core.service.api.Service;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class LocalEnvironmentVerifier {
    private final NetworkOperator networkOperator;

    void verifyEnvironmentStarted(@NonNull Environment environment) {
        var externalConfigurations = extractConfigurations(environment);

        var application = environment.getApplication();
        verify(application).start(new ApplicationStartupContext(externalConfigurations));

        environment
                .getServiceRegistry()
                .getEntries()
                .forEach(service -> verify(service).start(any()));

        verify(networkOperator).start(any());
    }

    void verifyEnvironmentShutdown(@NonNull Environment environment) {
        var application = environment.getApplication();
        verify(application).shutdown();

        environment
                .getServiceRegistry()
                .getEntries()
                .forEach(service -> verify(service).shutdown());

        verify(networkOperator).shutdown();
    }

    private Set<ConfigurationEntry> extractConfigurations(Environment environment) {
        var applicationConfigurations = environment.getConfigurationRegistry().getEntries();
        var servicesConfigurations = environment
                .getServiceRegistry()
                .getEntries()
                .stream()
                .map(Service::getConfigurationRegistry)
                .map(ConfigurationRegistry::getEntries)
                .flatMap(Collection::stream)
                .collect(Collectors.toUnmodifiableSet());

        return CollectionHelper.mergeSets(applicationConfigurations, servicesConfigurations);
    }

}
