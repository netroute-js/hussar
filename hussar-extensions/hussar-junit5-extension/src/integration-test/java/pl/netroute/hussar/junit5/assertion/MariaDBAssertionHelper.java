package pl.netroute.hussar.junit5.assertion;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import pl.netroute.hussar.core.api.application.Application;
import pl.netroute.hussar.core.helper.EndpointHelper;
import pl.netroute.hussar.junit5.helper.ApplicationClientRunner;
import pl.netroute.hussar.service.sql.MariaDBDockerService;

import static pl.netroute.hussar.junit5.assertion.ApplicationPropertiesAssertionHelper.assertPropertyConfigured;
import static pl.netroute.hussar.junit5.assertion.SQLDatabaseAssertionHelper.assertDatabaseReachable;
import static pl.netroute.hussar.junit5.config.ApplicationProperties.MARIADB_ALTERNATIVE_PASSWORD_PROPERTY;
import static pl.netroute.hussar.junit5.config.ApplicationProperties.MARIADB_ALTERNATIVE_URL_PROPERTY;
import static pl.netroute.hussar.junit5.config.ApplicationProperties.MARIADB_ALTERNATIVE_USERNAME_PROPERTY;
import static pl.netroute.hussar.junit5.config.ApplicationProperties.MARIADB_PASSWORD_PROPERTY;
import static pl.netroute.hussar.junit5.config.ApplicationProperties.MARIADB_URL_PROPERTY;
import static pl.netroute.hussar.junit5.config.ApplicationProperties.MARIADB_USERNAME_PROPERTY;
import static pl.netroute.hussar.junit5.factory.MySQLServiceFactory.SCHEMA;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MariaDBAssertionHelper {

    public static void assertMariaDBBootstrapped(@NonNull MariaDBDockerService mariaDBService,
                                                 @NonNull Application application) {
        var endpoint = EndpointHelper.getAnyEndpointOrFail(mariaDBService);
        var credentials = mariaDBService.getCredentials();
        var applicationClientRunner = new ApplicationClientRunner(application);

        assertDatabaseReachable(endpoint, SCHEMA, credentials);
        applicationClientRunner.run(applicationClient -> assertPropertyConfigured(MARIADB_URL_PROPERTY, endpoint.address(), applicationClient));
        applicationClientRunner.run(applicationClient -> assertPropertyConfigured(MARIADB_ALTERNATIVE_URL_PROPERTY, endpoint.address(), applicationClient));
        applicationClientRunner.run(applicationClient -> assertPropertyConfigured(MARIADB_USERNAME_PROPERTY, credentials.username(), applicationClient));
        applicationClientRunner.run(applicationClient -> assertPropertyConfigured(MARIADB_ALTERNATIVE_USERNAME_PROPERTY, credentials.username(), applicationClient));
        applicationClientRunner.run(applicationClient -> assertPropertyConfigured(MARIADB_PASSWORD_PROPERTY, credentials.password(), applicationClient));
        applicationClientRunner.run(applicationClient -> assertPropertyConfigured(MARIADB_ALTERNATIVE_PASSWORD_PROPERTY, credentials.password(), applicationClient));
    }

}