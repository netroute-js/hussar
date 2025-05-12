package pl.netroute.hussar.service.nosql.mongodb.api;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import pl.netroute.hussar.core.api.Endpoint;
import pl.netroute.hussar.core.configuration.api.ConfigurationEntry;
import pl.netroute.hussar.core.configuration.api.ConfigurationRegistry;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class MongoDBEndpointWithCredentialsRegistererVerifier {
    private static final String JOIN_DELIMITER = ",";

    private final ConfigurationRegistry configurationRegistry;

    void assertRegisteredEndpointWithCredentialsUnderProperty(@NonNull String endpointProperty,
                                                              @NonNull Endpoint endpoint,
                                                              @NonNull MongoDBCredentials credentials) {
        var formattedEndpoint = MongoDBEndpointFormatter.format(endpoint, credentials);
        var registeredEntry = ConfigurationEntry.property(endpointProperty, formattedEndpoint);

        assertRegisteredEntries(List.of(registeredEntry));
    }

    void assertRegisteredEndpointsWithCredentialsUnderProperty(@NonNull String endpointProperty,
                                                               @NonNull List<Endpoint> endpoints,
                                                               @NonNull MongoDBCredentials credentials) {
        var formattedEndpoints = endpoints
                .stream()
                .map(endpoint -> MongoDBEndpointFormatter.format(endpoint, credentials))
                .collect(Collectors.joining(JOIN_DELIMITER));

        var registeredEntry = ConfigurationEntry.property(endpointProperty, formattedEndpoints);

        assertRegisteredEntries(List.of(registeredEntry));
    }

    void assertRegisteredEndpointWithCredentialsUnderEnvVariable(@NonNull String endpointEnvVariable,
                                                                 @NonNull Endpoint endpoint,
                                                                 @NonNull MongoDBCredentials credentials) {
        var formattedEndpoint = MongoDBEndpointFormatter.format(endpoint, credentials);
        var registeredEntry = ConfigurationEntry.envVariable(endpointEnvVariable, formattedEndpoint);

        assertRegisteredEntries(List.of(registeredEntry));
    }

    void assertRegisteredEndpointsWithCredentialsUnderEnvVariable(@NonNull String endpointEndVariable,
                                                                  @NonNull List<Endpoint> endpoints,
                                                                  @NonNull MongoDBCredentials credentials) {
        var formattedEndpoints = endpoints
                .stream()
                .map(endpoint -> MongoDBEndpointFormatter.format(endpoint, credentials))
                .collect(Collectors.joining(JOIN_DELIMITER));

        var registeredEntry = ConfigurationEntry.envVariable(endpointEndVariable, formattedEndpoints);

        assertRegisteredEntries(List.of(registeredEntry));
    }

    private void assertRegisteredEntries(List<? extends ConfigurationEntry> entries) {
        assertThat(configurationRegistry.getEntries()).containsExactlyInAnyOrderElementsOf(entries);
    }

}