package pl.netroute.hussar.core.service;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import pl.netroute.hussar.core.Endpoint;
import pl.netroute.hussar.core.api.ConfigurationRegistry;
import pl.netroute.hussar.core.api.EnvVariableConfigurationEntry;
import pl.netroute.hussar.core.api.PropertyConfigurationEntry;

import java.util.Optional;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class EndpointRegisterer {

    @NonNull
    private final ConfigurationRegistry configurationRegistry;

    void registerUnderProperty(@NonNull Endpoint endpoint,
                               @NonNull String endpointProperty) {
        Optional
                .ofNullable(endpointProperty)
                .map(property -> new PropertyConfigurationEntry(property, endpoint.getAddress()))
                .ifPresent(configurationRegistry::register);
    }

    void registerUnderEnvironmentVariable(@NonNull Endpoint endpoint,
                                          @NonNull String endpointEnvVariable) {
        Optional
                .ofNullable(endpointEnvVariable)
                .map(envVariable -> new EnvVariableConfigurationEntry(envVariable, endpoint.getAddress()))
                .ifPresent(configurationRegistry::register);
    }

}
