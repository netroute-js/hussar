package pl.netroute.hussar.core.service.registerer;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import pl.netroute.hussar.core.api.Endpoint;
import pl.netroute.hussar.core.api.configuration.ConfigurationRegistry;
import pl.netroute.hussar.core.api.configuration.EnvVariableConfigurationEntry;
import pl.netroute.hussar.core.api.configuration.PropertyConfigurationEntry;

/**
 * A custom {@link Endpoint} registerer in {@link ConfigurationRegistry}.
 */
@RequiredArgsConstructor
public class EndpointRegisterer {

    @NonNull
    private final ConfigurationRegistry configurationRegistry;

    /**
     * Registers {@link Endpoint} under given property in {@link ConfigurationRegistry}.
     *
     * @param endpoint - the {@link Endpoint} to be registered.
     * @param endpointProperty - the property used to register given {@link Endpoint} under.
     */
    public void registerUnderProperty(@NonNull Endpoint endpoint,
                                      @NonNull String endpointProperty) {
        var property = new PropertyConfigurationEntry(endpointProperty, endpoint.address());

        configurationRegistry.register(property);
    }

    /**
     * Registers {@link Endpoint} under given environment variable in {@link ConfigurationRegistry}.
     *
     * @param endpoint - the {@link Endpoint} to be registered.
     * @param endpointEnvVariable - the environment variable used to register given {@link Endpoint} under.
     */
    public void registerUnderEnvironmentVariable(@NonNull Endpoint endpoint,
                                                 @NonNull String endpointEnvVariable) {
        var envVariable = new EnvVariableConfigurationEntry(endpointEnvVariable, endpoint.address());

        configurationRegistry.register(envVariable);
    }

}
