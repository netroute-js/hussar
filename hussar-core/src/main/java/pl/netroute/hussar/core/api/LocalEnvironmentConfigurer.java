package pl.netroute.hussar.core.api;

import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static pl.netroute.hussar.core.api.ConfigurationEntry.envVariable;
import static pl.netroute.hussar.core.api.ConfigurationEntry.property;

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
        var serviceRegistry = new MapServiceRegistry(services);

        var configurations = mergeConfigurations();
        var configurationRegistry = new MapConfigurationRegistry(configurations);

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
