package pl.netroute.hussar.junit5.assertion;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import pl.netroute.hussar.core.application.api.Application;
import pl.netroute.hussar.core.helper.EndpointHelper;
import pl.netroute.hussar.junit5.helper.ApplicationClientRunner;
import pl.netroute.hussar.service.sql.PostgreSQLDockerService;

import static pl.netroute.hussar.junit5.assertion.ApplicationPropertiesAssertionHelper.assertPropertyConfigured;
import static pl.netroute.hussar.junit5.assertion.SQLDatabaseAssertionHelper.assertDatabaseReachable;
import static pl.netroute.hussar.junit5.config.ApplicationProperties.POSTGRESQL_ALTERNATIVE_PASSWORD_PROPERTY;
import static pl.netroute.hussar.junit5.config.ApplicationProperties.POSTGRESQL_ALTERNATIVE_URL_PROPERTY;
import static pl.netroute.hussar.junit5.config.ApplicationProperties.POSTGRESQL_ALTERNATIVE_USERNAME_PROPERTY;
import static pl.netroute.hussar.junit5.config.ApplicationProperties.POSTGRESQL_PASSWORD_PROPERTY;
import static pl.netroute.hussar.junit5.config.ApplicationProperties.POSTGRESQL_URL_PROPERTY;
import static pl.netroute.hussar.junit5.config.ApplicationProperties.POSTGRESQL_USERNAME_PROPERTY;
import static pl.netroute.hussar.junit5.factory.MySQLServiceFactory.SCHEMA;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PostgreSQLAssertionHelper {

    public static void assertPostgreSQLBootstrapped(@NonNull PostgreSQLDockerService postgreSQLService,
                                                    @NonNull Application application) {
        var endpoint = EndpointHelper.getAnyEndpointOrFail(postgreSQLService);
        var credentials = postgreSQLService.getCredentials();
        var applicationClientRunner = new ApplicationClientRunner(application);

        assertDatabaseReachable(endpoint, SCHEMA, credentials);
        applicationClientRunner.run(applicationClient -> assertPropertyConfigured(POSTGRESQL_URL_PROPERTY, endpoint.address(), applicationClient));
        applicationClientRunner.run(applicationClient -> assertPropertyConfigured(POSTGRESQL_ALTERNATIVE_URL_PROPERTY, endpoint.address(), applicationClient));
        applicationClientRunner.run(applicationClient -> assertPropertyConfigured(POSTGRESQL_USERNAME_PROPERTY, credentials.username(), applicationClient));
        applicationClientRunner.run(applicationClient -> assertPropertyConfigured(POSTGRESQL_ALTERNATIVE_USERNAME_PROPERTY, credentials.username(), applicationClient));
        applicationClientRunner.run(applicationClient -> assertPropertyConfigured(POSTGRESQL_PASSWORD_PROPERTY, credentials.password(), applicationClient));
        applicationClientRunner.run(applicationClient -> assertPropertyConfigured(POSTGRESQL_ALTERNATIVE_PASSWORD_PROPERTY, credentials.password(), applicationClient));
    }

}