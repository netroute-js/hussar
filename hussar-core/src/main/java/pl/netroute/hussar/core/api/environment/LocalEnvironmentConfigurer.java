package pl.netroute.hussar.core.api.environment;

import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;
import pl.netroute.hussar.core.api.application.Application;
import pl.netroute.hussar.core.api.configuration.ConfigurationEntry;
import pl.netroute.hussar.core.api.configuration.DefaultConfigurationRegistry;
import pl.netroute.hussar.core.api.service.DefaultServiceRegistry;
import pl.netroute.hussar.core.api.service.Service;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static pl.netroute.hussar.core.api.configuration.ConfigurationEntry.envVariable;
import static pl.netroute.hussar.core.api.configuration.ConfigurationEntry.property;

/**
 * An actual implementation of {@link EnvironmentConfigurer}.
 */
@Builder(builderMethodName = "newInstance", buildMethodName = "done", setterPrefix = "with")
public final class LocalEnvironmentConfigurer implements EnvironmentConfigurer {

    @NonNull
    private final Application application;

    @Singular
    private final Set<Service> services;

    @Singular
    private final Map<String, String> properties;

    @Singular
    private final Map<String, String> environmentVariables;

    @Override
    public Environment configure() {
        var serviceRegistry = new DefaultServiceRegistry(services);

        var configurations = mergeConfigurations();
        var configurationRegistry = new DefaultConfigurationRegistry(configurations);

        return new DefaultEnvironment(
                application,
                configurationRegistry,
                serviceRegistry
        );
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