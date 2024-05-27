package pl.netroute.hussar.core.service.registerer;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import pl.netroute.hussar.core.api.Endpoint;
import pl.netroute.hussar.core.api.ConfigurationRegistry;
import pl.netroute.hussar.core.api.EnvVariableConfigurationEntry;
import pl.netroute.hussar.core.api.PropertyConfigurationEntry;

import java.util.Optional;

@RequiredArgsConstructor
public class EndpointRegisterer {

    @NonNull
    private final ConfigurationRegistry configurationRegistry;

    public void registerUnderProperty(@NonNull Endpoint endpoint,
                                      @NonNull String endpointProperty) {
        Optional
                .ofNullable(endpointProperty)
                .map(property -> new PropertyConfigurationEntry(property, endpoint.address()))
                .ifPresent(configurationRegistry::register);
    }

    public void registerUnderEnvironmentVariable(@NonNull Endpoint endpoint,
                                                 @NonNull String endpointEnvVariable) {
        Optional
                .ofNullable(endpointEnvVariable)
                .map(envVariable -> new EnvVariableConfigurationEntry(envVariable, endpoint.address()))
                .ifPresent(configurationRegistry::register);
    }

}
