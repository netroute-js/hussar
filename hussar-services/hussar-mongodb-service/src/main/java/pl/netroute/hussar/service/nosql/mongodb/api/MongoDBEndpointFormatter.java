package pl.netroute.hussar.service.nosql.mongodb.api;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import pl.netroute.hussar.core.api.Endpoint;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class MongoDBEndpointFormatter {
    private static final String ENDPOINT_WITH_CREDENTIALS_TEMPLATE = "%s%s:%s@%s:%d";

    static String format(@NonNull Endpoint endpoint,
                         @NonNull MongoDBCredentials credentials) {
        var username = credentials.username();
        var password = credentials.password();

        var schema = endpoint.scheme();
        var host = endpoint.host();
        var port = endpoint.port();

        return ENDPOINT_WITH_CREDENTIALS_TEMPLATE.formatted(schema, username, password, host, port);
    }

}
