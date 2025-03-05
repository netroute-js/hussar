package pl.netroute.hussar.core.service.registerer;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import pl.netroute.hussar.core.api.Endpoint;
import pl.netroute.hussar.core.api.InternalUseOnly;
import pl.netroute.hussar.core.configuration.api.ConfigurationRegistry;
import pl.netroute.hussar.core.configuration.api.EnvVariableConfigurationEntry;
import pl.netroute.hussar.core.configuration.api.PropertyConfigurationEntry;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A custom {@link Endpoint} registerer in {@link ConfigurationRegistry}.
 */
@InternalUseOnly
@RequiredArgsConstructor
public class EndpointRegisterer {
    public static final String JOIN_DELIMITER = ",";

    @NonNull
    private final ConfigurationRegistry configurationRegistry;

    /**
     * Registers list of {@link Endpoint} under given property in {@link ConfigurationRegistry}.
     *
     * @param endpoints - the list of {@link Endpoint} to be registered.
     * @param endpointProperty - the property used to register given {@link Endpoint} under.
     */
    public void registerUnderProperty(@NonNull List<Endpoint> endpoints,
                                      @NonNull String endpointProperty) {
        var formattedEndpoints = formatEndpoints(endpoints);
        var property = new PropertyConfigurationEntry(endpointProperty, formattedEndpoints);

        configurationRegistry.register(property);
    }

    /**
     * Registers list of {@link Endpoint} under given environment variable in {@link ConfigurationRegistry}.
     *
     * @param endpoints - the list of {@link Endpoint} to be registered.
     * @param endpointEnvVariable - the environment variable used to register given {@link Endpoint} under.
     */
    public void registerUnderEnvironmentVariable(@NonNull List<Endpoint> endpoints,
                                                 @NonNull String endpointEnvVariable) {
        var formattedEndpoints = formatEndpoints(endpoints);
        var envVariable = new EnvVariableConfigurationEntry(endpointEnvVariable, formattedEndpoints);

        configurationRegistry.register(envVariable);
    }

    private String formatEndpoints(List<Endpoint> endpoints) {
        return endpoints
                .stream()
                .map(Endpoint::address)
                .collect(Collectors.joining(JOIN_DELIMITER));
    }

}
