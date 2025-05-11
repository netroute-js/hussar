package pl.netroute.hussar.service.nosql.mongodb.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.netroute.hussar.core.api.Endpoint;

public class MongoDBEndpointFormatterTest {
    private static final String MONGODB_SCHEME = "mongodb://";

    private MongoDBEndpointFormatterVerifier verifier;

    @BeforeEach
    public void setup() {
        verifier = new MongoDBEndpointFormatterVerifier();
    }

    @Test
    public void shouldFormatEndpoint() {
        // given
        var endpoint = Endpoint.of(MONGODB_SCHEME, "localhost", 27017);
        var credentials = new MongoDBCredentials("mongo-user", "a-password");

        // when
        var formattedEndpoint = MongoDBEndpointFormatter.format(endpoint, credentials);

        // then
        verifier.assertEndpointFormatted(formattedEndpoint, endpoint, credentials);
    }

}
