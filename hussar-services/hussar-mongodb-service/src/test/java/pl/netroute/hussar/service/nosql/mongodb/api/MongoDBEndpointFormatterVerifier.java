package pl.netroute.hussar.service.nosql.mongodb.api;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import pl.netroute.hussar.core.api.Endpoint;

import static org.assertj.core.api.Assertions.assertThat;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
class MongoDBEndpointFormatterVerifier {
    private static final String ENDPOINT_WITH_CREDENTIALS_TEMPLATE = "%s%s:%s@%s:%d";

    void assertEndpointFormatted(@NonNull String formattedEndpoint,
                                 @NonNull Endpoint endpoint,
                                 @NonNull MongoDBCredentials credentials) {
        var username = credentials.username();
        var password = credentials.password();

        var schema = endpoint.scheme();
        var host = endpoint.host();
        var port = endpoint.port();

        var expectedFormattedEndpoint = ENDPOINT_WITH_CREDENTIALS_TEMPLATE.formatted(schema, username, password, host, port);
        assertThat(formattedEndpoint).isEqualTo(expectedFormattedEndpoint);
    }

}