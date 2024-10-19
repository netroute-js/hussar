package pl.netroute.hussar.core.service.registerer;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import pl.netroute.hussar.core.api.Endpoint;
import pl.netroute.hussar.core.api.ConfigurationRegistry;
import pl.netroute.hussar.core.api.EnvVariableConfigurationEntry;
import pl.netroute.hussar.core.api.PropertyConfigurationEntry;

import java.util.Optional;

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
        Optional
                .ofNullable(endpointProperty)
                .map(property -> new PropertyConfigurationEntry(property, endpoint.address()))
                .ifPresent(configurationRegistry::register);
    }

    /**
     * Registers {@link Endpoint} under given environment variable in {@link ConfigurationRegistry}.
     *
     * @param endpoint - the {@link Endpoint} to be registered.
     * @param endpointEnvVariable - the environment variable used to register given {@link Endpoint} under.
     */
    public void registerUnderEnvironmentVariable(@NonNull Endpoint endpoint,
                                                 @NonNull String endpointEnvVariable) {
        Optional
                .ofNullable(endpointEnvVariable)
                .map(envVariable -> new EnvVariableConfigurationEntry(envVariable, endpoint.address()))
                .ifPresent(configurationRegistry::register);
    }

}
