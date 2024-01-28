package pl.netroute.hussar.service.sql;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import pl.netroute.hussar.core.api.ServiceStartupContext;
import pl.netroute.hussar.core.helper.EndpointHelper;
import pl.netroute.hussar.service.sql.api.DatabaseSchema;
import pl.netroute.hussar.service.sql.assertion.DatabaseAssertionHelper;

import java.util.List;
import java.util.Optional;

public class MySqlDockerServiceTest {
    private static final List<String> TABLES = List.of("TableA", "TableB");

    private MySqlDockerService mysqlService;

    @AfterEach
    public void cleanup() {
        Optional
                .ofNullable(mysqlService)
                .ifPresent(MySqlDockerService::shutdown);
    }

    @Test
    public void shouldStartMysqlService() {
        // given
        var schemaName = "HussarDB";
        var databaseSchema = DatabaseSchema.scriptLess(schemaName);

        mysqlService = MySqlDockerServiceConfigurer
                .newInstance()
                .databaseSchema(databaseSchema)
                .done()
                .configure();

        // when
        mysqlService.start(ServiceStartupContext.empty());

        // then
        var databaseAssertion = new DatabaseAssertionHelper(mysqlService);
        databaseAssertion.assertSingleEndpoint();
        databaseAssertion.asserDatabaseAccessible(schemaName);
        databaseAssertion.assertTablesNotCreated(schemaName, TABLES);
        databaseAssertion.assertNoEntriesRegistered();
    }

    @Test
    public void shouldStartMysqlServiceWithFullConfiguration() {
        // given
        var name = "mysql-instance";
        var dockerVersion = "8.2.0";

        var endpointProperty = "mysql.url";
        var endpointEnvVariable = "MYSQL_URL";

        var usernameProperty = "mysql.username";
        var usernameEnvVariable = "MYSQL_USERNAME";

        var passwordProperty = "mysql.password";
        var passwordEnvVariable = "MYSQL_PASSWORD";

        var schemaName = "HussarDB";
        var scriptsLocation = "/flyway/scripts";
        var databaseSchema = new DatabaseSchema(schemaName, scriptsLocation);

        mysqlService = MySqlDockerServiceConfigurer
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
                .configure();

        // when
        mysqlService.start(ServiceStartupContext.empty());

        // then
        var databaseAssertion = new DatabaseAssertionHelper(mysqlService);
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
    public void shouldShutdownMysqlService() {
        var name = "mysql-instance";
        var dockerVersion = "8.2.0";

        var schemaName = "HussarDB";
        var databaseSchema = DatabaseSchema.scriptLess(schemaName);

        mysqlService = MySqlDockerServiceConfigurer
                .newInstance()
                .name(name)
                .databaseSchema(databaseSchema)
                .dockerImageVersion(dockerVersion)
                .done()
                .configure();

        // when
        mysqlService.start(ServiceStartupContext.empty());

        var endpoint = EndpointHelper.getAnyEndpointOrFail(mysqlService);

        mysqlService.shutdown();

        // then
        var databaseAssertion = new DatabaseAssertionHelper(mysqlService);
        databaseAssertion.assertDatabaseNotAccessible(schemaName, endpoint);
    }
}
