package pl.netroute.hussar.service.sql;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import pl.netroute.hussar.core.service.ServiceConfigureContext;
import pl.netroute.hussar.core.service.ServiceStartupContext;
import pl.netroute.hussar.core.helper.EndpointHelper;
import pl.netroute.hussar.service.sql.api.MySQLDockerService;
import pl.netroute.hussar.service.sql.api.MySQLDockerServiceConfigurer;
import pl.netroute.hussar.service.sql.api.SQLDatabaseSchema;
import pl.netroute.hussar.service.sql.assertion.SQLDBAssertionHelper;

import java.util.List;
import java.util.Optional;

public class MySQLDockerServiceIT {
    private static final List<String> TABLES = List.of("table_a", "table_b");

    private MySQLDockerService databaseService;

    @AfterEach
    public void cleanup() {
        Optional
                .ofNullable(databaseService)
                .ifPresent(MySQLDockerService::shutdown);
    }

    @Test
    public void shouldStartDatabaseService() {
        // given
        var schemaName = "hussardb";
        var databaseSchema = SQLDatabaseSchema.scriptLess(schemaName);

        databaseService = MySQLDockerServiceConfigurer
                .newInstance()
                .databaseSchema(databaseSchema)
                .done()
                .configure(ServiceConfigureContext.defaultContext());

        // when
        databaseService.start(ServiceStartupContext.defaultContext());

        // then
        var databaseAssertion = new SQLDBAssertionHelper(databaseService);
        databaseAssertion.assertSingleEndpoint();
        databaseAssertion.asserDatabaseAccessible(schemaName);
        databaseAssertion.assertTablesNotCreated(schemaName, TABLES);
        databaseAssertion.assertNoEntriesRegistered();
    }

    @Test
    public void shouldStartDatabaseServiceWithFullConfiguration() {
        // given
        var name = "mysql-instance";
        var dockerVersion = "8.2.0";

        var endpointProperty = "mysql.url";
        var endpointEnvVariable = "MYSQL_URL";

        var usernameProperty = "mysql.username";
        var usernameEnvVariable = "MYSQL_USERNAME";

        var passwordProperty = "mysql.password";
        var passwordEnvVariable = "MYSQL_PASSWORD";

        var schemaName = "hussardb";
        var scriptsLocation = "/flyway/scripts";
        var databaseSchema = new SQLDatabaseSchema(schemaName, scriptsLocation);

        databaseService = MySQLDockerServiceConfigurer
                .newInstance()
                .name(name)
                .dockerImageVersion(dockerVersion)
                .databaseSchema(databaseSchema)
                .registerEndpointUnderProperty(endpointProperty)
                .registerEndpointUnderEnvironmentVariable(endpointEnvVariable)
                .registerUsernameUnderProperty(usernameProperty)
                .registerUsernameUnderEnvironmentVariable(usernameEnvVariable)
                .registerPasswordUnderProperty(passwordProperty)
                .registerPasswordUnderEnvironmentVariable(passwordEnvVariable)
                .done()
                .configure(ServiceConfigureContext.defaultContext());

        // when
        databaseService.start(ServiceStartupContext.defaultContext());

        // then
        var databaseAssertion = new SQLDBAssertionHelper(databaseService);
        databaseAssertion.assertSingleEndpoint();
        databaseAssertion.asserDatabaseAccessible(schemaName);
        databaseAssertion.assertTablesCreated(schemaName, TABLES);
        databaseAssertion.assertRegisteredEndpointUnderProperty(endpointProperty);
        databaseAssertion.assertRegisteredEndpointUnderEnvironmentVariable(endpointEnvVariable);
        databaseAssertion.assertRegisteredUsernameUnderProperty(usernameProperty);
        databaseAssertion.assertRegisteredUsernameUnderEnvironmentVariable(usernameEnvVariable);
        databaseAssertion.assertRegisteredPasswordUnderProperty(passwordProperty);
        databaseAssertion.assertRegisteredPasswordUnderEnvironmentVariable(passwordEnvVariable);
    }

    @Test
    public void shouldShutdownDatabaseService() {
        var name = "mysql-instance";
        var dockerVersion = "8.2.0";

        var schemaName = "hussardb";
        var databaseSchema = SQLDatabaseSchema.scriptLess(schemaName);

        databaseService = MySQLDockerServiceConfigurer
                .newInstance()
                .name(name)
                .databaseSchema(databaseSchema)
                .dockerImageVersion(dockerVersion)
                .done()
                .configure(ServiceConfigureContext.defaultContext());

        // when
        databaseService.start(ServiceStartupContext.defaultContext());

        var endpoint = EndpointHelper.getAnyEndpointOrFail(databaseService);

        databaseService.shutdown();

        // then
        var databaseAssertion = new SQLDBAssertionHelper(databaseService);
        databaseAssertion.assertDatabaseNotAccessible(schemaName, endpoint);
    }
}
