package pl.netroute.hussar.junit5.assertion;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import pl.netroute.hussar.core.application.api.Application;
import pl.netroute.hussar.core.helper.EndpointHelper;
import pl.netroute.hussar.junit5.helper.ApplicationClientRunner;
import pl.netroute.hussar.service.sql.api.MySQLDockerService;

import static pl.netroute.hussar.junit5.assertion.ApplicationPropertiesAssertionHelper.assertPropertyConfigured;
import static pl.netroute.hussar.junit5.assertion.NetworkControlAssertionHelper.assertNetworkControlConfigured;
import static pl.netroute.hussar.junit5.assertion.SQLDatabaseAssertionHelper.assertDatabaseReachable;
import static pl.netroute.hussar.junit5.config.ApplicationProperties.MYSQL_ALTERNATIVE_PASSWORD_PROPERTY;
import static pl.netroute.hussar.junit5.config.ApplicationProperties.MYSQL_ALTERNATIVE_URL_PROPERTY;
import static pl.netroute.hussar.junit5.config.ApplicationProperties.MYSQL_ALTERNATIVE_USERNAME_PROPERTY;
import static pl.netroute.hussar.junit5.config.ApplicationProperties.MYSQL_PASSWORD_PROPERTY;
import static pl.netroute.hussar.junit5.config.ApplicationProperties.MYSQL_URL_PROPERTY;
import static pl.netroute.hussar.junit5.config.ApplicationProperties.MYSQL_USERNAME_PROPERTY;
import static pl.netroute.hussar.junit5.factory.MySQLServiceFactory.SCHEMA;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MySQLAssertionHelper {

    public static void assertMySQLBootstrapped(@NonNull MySQLDockerService mysqlService,
                                               @NonNull Application application) {
        var endpoint = EndpointHelper.getAnyEndpointOrFail(mysqlService);
        var credentials = mysqlService.getCredentials();
        var applicationClientRunner = new ApplicationClientRunner(application);

        assertDatabaseReachable(endpoint, SCHEMA, credentials);
        assertNetworkControlConfigured(mysqlService);

        applicationClientRunner.run(applicationClient -> assertPropertyConfigured(MYSQL_URL_PROPERTY, endpoint.address(), applicationClient));
        applicationClientRunner.run(applicationClient -> assertPropertyConfigured(MYSQL_ALTERNATIVE_URL_PROPERTY, endpoint.address(), applicationClient));
        applicationClientRunner.run(applicationClient -> assertPropertyConfigured(MYSQL_USERNAME_PROPERTY, credentials.username(), applicationClient));
        applicationClientRunner.run(applicationClient -> assertPropertyConfigured(MYSQL_ALTERNATIVE_USERNAME_PROPERTY, credentials.username(), applicationClient));
        applicationClientRunner.run(applicationClient -> assertPropertyConfigured(MYSQL_PASSWORD_PROPERTY, credentials.password(), applicationClient));
        applicationClientRunner.run(applicationClient -> assertPropertyConfigured(MYSQL_ALTERNATIVE_PASSWORD_PROPERTY, credentials.password(), applicationClient));
    }

    public static void assertMySQLBootstrapped(@NonNull MySQLDockerService mysqlService) {
        var endpoint = EndpointHelper.getAnyEndpointOrFail(mysqlService);
        var credentials = mysqlService.getCredentials();

        assertDatabaseReachable(endpoint, SCHEMA, credentials);
    }

}