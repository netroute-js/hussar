package pl.netroute.hussar.core.api;

import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Builder(builderMethodName = "newInstance", buildMethodName = "done", setterPrefix = "with")
public final class LocalEnvironmentConfigurer implements EnvironmentConfigurer {

    @NonNull
    private final Application application;

    @Singular
    private final Set<Service> services;

    @Singular
    private final List<PropertyConfigurationEntry> properties;

    @Singular
    private final List<EnvVariableConfigurationEntry> environmentVariables;

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
        return Stream
                .concat(properties.stream(), environmentVariables.stream())
                .collect(Collectors.toUnmodifiableSet());
    }

}
