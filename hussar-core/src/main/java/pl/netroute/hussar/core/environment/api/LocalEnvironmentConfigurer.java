package pl.netroute.hussar.core.environment.api;

import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;
import pl.netroute.hussar.core.application.api.Application;
import pl.netroute.hussar.core.configuration.api.ConfigurationEntry;
import pl.netroute.hussar.core.configuration.api.DefaultConfigurationRegistry;
import pl.netroute.hussar.core.docker.api.DockerRegistry;
import pl.netroute.hussar.core.service.api.DefaultServiceRegistry;
import pl.netroute.hussar.core.service.api.Service;
import pl.netroute.hussar.core.service.api.ServiceConfigureContext;
import pl.netroute.hussar.core.service.api.ServiceConfigurer;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static pl.netroute.hussar.core.configuration.api.ConfigurationEntry.envVariable;
import static pl.netroute.hussar.core.configuration.api.ConfigurationEntry.property;

/**
 * An actual implementation of {@link EnvironmentConfigurer}.
 */
@Builder(builderMethodName = "newInstance", buildMethodName = "done", setterPrefix = "with")
public final class LocalEnvironmentConfigurer implements EnvironmentConfigurer {

    @NonNull
    private final Application application;

    @NonNull
    @Builder.Default
    private final DockerRegistry dockerRegistry = DockerRegistry.defaultRegistry();

    @Singular
    private final Set<ServiceConfigurer<? extends Service>> services;

    @Singular
    private final Map<String, String> properties;

    @Singular
    private final Map<String, String> environmentVariables;

    @Override
    public Environment configure() {
        var configuredServices = configureServices();
        var serviceRegistry = DefaultServiceRegistry.of(configuredServices);

        var configurations = mergeConfigurations();
        var configurationRegistry = new DefaultConfigurationRegistry(configurations);

        return new DefaultEnvironment(
                application,
                configurationRegistry,
                serviceRegistry
        );
    }

    private Set<Service> configureServices() {
        var context = new ServiceConfigureContext(dockerRegistry);

        return services
                .stream()
                .map(serviceConfigurer -> serviceConfigurer.configure(context))
                .collect(Collectors.toUnmodifiableSet());
    }

    private Set<ConfigurationEntry> mergeConfigurations() {
        var propertiesEntries = properties
                .entrySet()
                .stream()
                .map(entry -> property(entry.getKey(), entry.getValue()))
                .toList();

        var environmentVariablesEntries = environmentVariables
                .entrySet()
                .stream()
                .map(entry -> envVariable(entry.getKey(), entry.getValue()))
                .toList();

        return Stream
                .concat(propertiesEntries.stream(), environmentVariablesEntries.stream())
                .collect(Collectors.toUnmodifiableSet());
    }

}
