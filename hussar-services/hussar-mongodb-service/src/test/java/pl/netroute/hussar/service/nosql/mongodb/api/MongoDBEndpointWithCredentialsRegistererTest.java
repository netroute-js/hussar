package pl.netroute.hussar.service.nosql.mongodb.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.netroute.hussar.core.api.Endpoint;
import pl.netroute.hussar.core.configuration.api.ConfigurationRegistry;
import pl.netroute.hussar.core.configuration.api.DefaultConfigurationRegistry;

import java.util.List;

public class MongoDBEndpointWithCredentialsRegistererTest {
    private static final String MONGODB_SCHEME = "mongodb://";

    private ConfigurationRegistry configurationRegistry;

    private MongoDBEndpointWithCredentialsRegisterer endpointRegisterer;
    private MongoDBEndpointWithCredentialsRegistererVerifier verifier;

    @BeforeEach
    public void setup() {
        configurationRegistry = new DefaultConfigurationRegistry();

        endpointRegisterer = new MongoDBEndpointWithCredentialsRegisterer(configurationRegistry);

        verifier = new MongoDBEndpointWithCredentialsRegistererVerifier(configurationRegistry);
    }

    @Test
    public void shouldRegisterSingleEndpointWithCredentialsUnderProperty() {
        // given
        var endpoint = Endpoint.of(MONGODB_SCHEME, "localhost", 27017);
        var credentials = new MongoDBCredentials("mongo-user", "a-password");
        var endpointProperty = "mongo.url";

        // when
        endpointRegisterer.registerUnderProperty(List.of(endpoint), credentials, endpointProperty);

        // then
        verifier.assertRegisteredEndpointWithCredentialsUnderProperty(endpointProperty, endpoint, credentials);
    }

    @Test
    public void shouldRegisterMultipleEndpointsWithCredentialsUnderProperty() {
        // given
        var firstEndpoint = Endpoint.of(MONGODB_SCHEME, "localhost", 27017);
        var secondEndpoint = Endpoint.of(MONGODB_SCHEME, "localhost", 27018);
        var endpoints = List.of(firstEndpoint, secondEndpoint);

        var credentials = new MongoDBCredentials("mongo-user", "a-password");
        var endpointProperty = "mongo.url";

        // when
        endpointRegisterer.registerUnderProperty(endpoints, credentials, endpointProperty);

        // then
        verifier.assertRegisteredEndpointsWithCredentialsUnderProperty(endpointProperty, endpoints, credentials);
    }

    @Test
    public void shouldRegisterSingleEndpointsWithCredentialsUnderEnvVariable() {
        // given
        var endpoint = Endpoint.of(MONGODB_SCHEME, "localhost", 27017);
        var credentials = new MongoDBCredentials("mongo-user", "a-password");
        var endpointEnvVariable = "MONGO_URL";

        // when
        endpointRegisterer.registerUnderEnvironmentVariable(List.of(endpoint), credentials, endpointEnvVariable);

        // then
        verifier.assertRegisteredEndpointWithCredentialsUnderEnvVariable(endpointEnvVariable, endpoint, credentials);
    }

    @Test
    public void shouldRegisterMultipleEndpointsWithCredentialsUnderEnvVariable() {
        // given
        var firstEndpoint = Endpoint.of(MONGODB_SCHEME, "localhost", 27017);
        var secondEndpoint = Endpoint.of(MONGODB_SCHEME, "localhost", 27018);
        var endpoints = List.of(firstEndpoint, secondEndpoint);

        var credentials = new MongoDBCredentials("mongo-user", "a-password");
        var endpointEnvVariable = "MONGO_URL";

        // when
        endpointRegisterer.registerUnderEnvironmentVariable(endpoints, credentials, endpointEnvVariable);

        // then
        verifier.assertRegisteredEndpointsWithCredentialsUnderEnvVariable(endpointEnvVariable, endpoints, credentials);
    }

}