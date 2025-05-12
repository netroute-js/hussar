package pl.netroute.hussar.service.nosql.mongodb.api;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import pl.netroute.hussar.core.api.Endpoint;
import pl.netroute.hussar.core.configuration.api.ConfigurationRegistry;
import pl.netroute.hussar.core.configuration.api.EnvVariableConfigurationEntry;
import pl.netroute.hussar.core.configuration.api.PropertyConfigurationEntry;
import pl.netroute.hussar.core.helper.StringHelper;

import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class MongoDBEndpointWithCredentialsRegisterer {
    private static final String JOIN_DELIMITER = ",";

    @NonNull
    private final ConfigurationRegistry configurationRegistry;

    void registerUnderProperty(@NonNull List<Endpoint> endpoints,
                               @NonNull MongoDBCredentials credentials,
                               @NonNull String endpointProperty) {
        var formattedEndpoints = StringHelper.join(endpoint -> MongoDBEndpointFormatter.format(endpoint, credentials), JOIN_DELIMITER, endpoints);
        var property = new PropertyConfigurationEntry(endpointProperty, formattedEndpoints);

        configurationRegistry.register(property);
    }

    void registerUnderEnvironmentVariable(@NonNull List<Endpoint> endpoints,
                                          @NonNull MongoDBCredentials credentials,
                                          @NonNull String endpointEnvVariable) {
        var formattedEndpoints = StringHelper.join(endpoint -> MongoDBEndpointFormatter.format(endpoint, credentials), JOIN_DELIMITER, endpoints);
        var envVariable = new EnvVariableConfigurationEntry(endpointEnvVariable, formattedEndpoints);

        configurationRegistry.register(envVariable);
    }

}
