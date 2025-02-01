package pl.netroute.hussar.service.sql;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import pl.netroute.hussar.core.service.api.ServiceConfigureContext;
import pl.netroute.hussar.core.service.api.ServiceStartupContext;
import pl.netroute.hussar.core.helper.EndpointHelper;
import pl.netroute.hussar.service.sql.api.SQLDatabaseSchema;
import pl.netroute.hussar.service.sql.assertion.SQLDBAssertionHelper;

import java.util.List;
import java.util.Optional;

public class PostgreSQLDockerServiceIT {
    private static final List<String> TABLES = List.of("table_a", "table_b");

    private PostgreSQLDockerService databaseService;

    @AfterEach
    public void cleanup() {
        Optional
                .ofNullable(databaseService)
                .ifPresent(PostgreSQLDockerService::shutdown);
    }

    @Test
    public void shouldStartDatabaseService() {
        // given
        var schemaName = "hussardb";
        var databaseSchema = SQLDatabaseSchema.scriptLess(schemaName);

        databaseService = PostgreSQLDockerServiceConfigurer
                .newInstance()
                .databaseSchema(databaseSchema)
                .done()
                .configure(new ServiceConfigureContext());

        // when
        databaseService.start(ServiceStartupContext.empty());

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
        var name = "postgres-instance";
        var dockerVersion = "16";

        var endpointProperty = "postgres.url";
        var endpointEnvVariable = "POSTGRES_URL";

        var usernameProperty = "postgres.username";
        var usernameEnvVariable = "POSTGRES_USERNAME";

        var passwordProperty = "postgres.password";
        var passwordEnvVariable = "POSTGRES_PASSWORD";

        var schemaName = "hussardb";
        var scriptsLocation = "/flyway/scripts";
        var databaseSchema = new SQLDatabaseSchema(schemaName, scriptsLocation);

        databaseService = PostgreSQLDockerServiceConfigurer
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
                .configure(new ServiceConfigureContext());

        // when
        databaseService.start(ServiceStartupContext.empty());

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
        var name = "postgres-instance";
        var dockerVersion = "16";

        var schemaName = "hussardb";
        var databaseSchema = SQLDatabaseSchema.scriptLess(schemaName);

        databaseService = PostgreSQLDockerServiceConfigurer
                .newInstance()
                .name(name)
                .databaseSchema(databaseSchema)
                .dockerImageVersion(dockerVersion)
                .done()
                .configure(new ServiceConfigureContext());

        // when
        databaseService.start(ServiceStartupContext.empty());

        var endpoint = EndpointHelper.getAnyEndpointOrFail(databaseService);

        databaseService.shutdown();

        // then
        var databaseAssertion = new SQLDBAssertionHelper(databaseService);
        databaseAssertion.assertDatabaseNotAccessible(schemaName, endpoint);
    }
}
