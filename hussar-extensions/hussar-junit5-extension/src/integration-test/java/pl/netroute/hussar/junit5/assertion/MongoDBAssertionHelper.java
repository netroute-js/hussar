package pl.netroute.hussar.junit5.assertion;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.connection.ClusterSettings;
import com.mongodb.connection.SocketSettings;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import pl.netroute.hussar.core.api.Endpoint;
import pl.netroute.hussar.core.api.application.Application;
import pl.netroute.hussar.core.helper.EndpointHelper;
import pl.netroute.hussar.junit5.helper.ApplicationClientRunner;
import pl.netroute.hussar.service.nosql.mongodb.MongoDBDockerService;
import pl.netroute.hussar.service.nosql.mongodb.api.MongoDBCredentials;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.netroute.hussar.junit5.assertion.ApplicationPropertiesAssertionHelper.assertPropertyConfigured;
import static pl.netroute.hussar.junit5.config.ApplicationProperties.MONGODB_ALTERNATIVE_PASSWORD_PROPERTY;
import static pl.netroute.hussar.junit5.config.ApplicationProperties.MONGODB_ALTERNATIVE_URL_PROPERTY;
import static pl.netroute.hussar.junit5.config.ApplicationProperties.MONGODB_ALTERNATIVE_USERNAME_PROPERTY;
import static pl.netroute.hussar.junit5.config.ApplicationProperties.MONGODB_PASSWORD_PROPERTY;
import static pl.netroute.hussar.junit5.config.ApplicationProperties.MONGODB_URL_PROPERTY;
import static pl.netroute.hussar.junit5.config.ApplicationProperties.MONGODB_USERNAME_PROPERTY;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MongoDBAssertionHelper {
    private static final int TIMEOUT_MILLIS = 5000;

    private static final String DEFAULT_AUTH_DB = "admin";

    public static void assertMongoDBBootstrapped(@NonNull MongoDBDockerService mongoDBService,
                                                 @NonNull Application application) {
        var endpoint = EndpointHelper.getAnyEndpointOrFail(mongoDBService);
        var credentials = mongoDBService.getCredentials();
        var applicationClientRunner = new ApplicationClientRunner(application);

        assertMongoDBReachable(endpoint, credentials);
        applicationClientRunner.run(applicationClient -> assertPropertyConfigured(MONGODB_URL_PROPERTY, endpoint.address(), applicationClient));
        applicationClientRunner.run(applicationClient -> assertPropertyConfigured(MONGODB_ALTERNATIVE_URL_PROPERTY, endpoint.address(), applicationClient));
        applicationClientRunner.run(applicationClient -> assertPropertyConfigured(MONGODB_USERNAME_PROPERTY, credentials.username(), applicationClient));
        applicationClientRunner.run(applicationClient -> assertPropertyConfigured(MONGODB_ALTERNATIVE_USERNAME_PROPERTY, credentials.username(), applicationClient));
        applicationClientRunner.run(applicationClient -> assertPropertyConfigured(MONGODB_PASSWORD_PROPERTY, credentials.password(), applicationClient));
        applicationClientRunner.run(applicationClient -> assertPropertyConfigured(MONGODB_ALTERNATIVE_PASSWORD_PROPERTY, credentials.password(), applicationClient));
    }

    private static void assertMongoDBReachable(Endpoint endpoint, MongoDBCredentials credentials) {
        try(var client = createClient(endpoint, credentials)) {
            assertThat(client.listDatabaseNames()).contains(DEFAULT_AUTH_DB);
        }
    }

    private static MongoClient createClient(Endpoint endpoint, MongoDBCredentials credentials) {
        var mongoURL = new ConnectionString(endpoint.address());
        var mongoCredentials = createCredentials(credentials);

        var mongoSettings = MongoClientSettings
                .builder()
                .credential(mongoCredentials)
                .applyToSocketSettings(MongoDBAssertionHelper::configureSocketSettings)
                .applyToClusterSettings(MongoDBAssertionHelper::configureClusterSettings)
                .applyConnectionString(mongoURL)
                .build();

        return MongoClients.create(mongoSettings);
    }

    private static MongoCredential createCredentials(MongoDBCredentials credentials) {
        var username = credentials.username();
        var password = credentials.password();

        return MongoCredential.createCredential(username, DEFAULT_AUTH_DB, password.toCharArray());
    }

    private static void configureSocketSettings(SocketSettings.Builder builder) {
        builder.connectTimeout(TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
                .readTimeout(TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
    }

    private static void configureClusterSettings(ClusterSettings.Builder builder) {
        builder.serverSelectionTimeout(TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
    }
}
