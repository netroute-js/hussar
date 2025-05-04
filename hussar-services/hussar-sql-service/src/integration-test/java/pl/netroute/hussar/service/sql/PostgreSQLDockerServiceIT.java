package pl.netroute.hussar.service.sql;

import pl.netroute.hussar.core.api.Endpoint;
import pl.netroute.hussar.core.service.BaseServiceIT;
import pl.netroute.hussar.core.service.ServiceConfigureContext;
import pl.netroute.hussar.service.sql.api.PostgreSQLDockerService;
import pl.netroute.hussar.service.sql.api.PostgreSQLDockerServiceConfigurer;
import pl.netroute.hussar.service.sql.api.SQLDatabaseSchema;
import pl.netroute.hussar.service.sql.assertion.SQLDBAssertionHelper;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

class PostgreSQLDockerServiceIT extends BaseServiceIT<PostgreSQLDockerService> {
    private static final List<String> TABLES = List.of("table_a", "table_b");

    @Override
    protected ServiceTestMetadata<PostgreSQLDockerService, Consumer<PostgreSQLDockerService>> provideMinimallyConfiguredServiceTestMetadata(ServiceConfigureContext context) {
        var schemaName = "hussardb";
        var databaseSchema = SQLDatabaseSchema.scriptLess(schemaName);

        var service = PostgreSQLDockerServiceTestFactory.createMinimallyConfigured(databaseSchema, context);

        var assertion = (Consumer<PostgreSQLDockerService>) actualService -> {
            var databaseAssertion = new SQLDBAssertionHelper(service);
            databaseAssertion.assertSingleEndpoint();
            databaseAssertion.asserDatabaseAccessible(schemaName);
            databaseAssertion.assertTablesNotCreated(schemaName, TABLES);
            databaseAssertion.assertNoEntriesRegistered();
        };

        return ServiceTestMetadata
                .<PostgreSQLDockerService, Consumer<PostgreSQLDockerService>>newInstance()
                .service(service)
                .assertion(assertion)
                .done();
    }

    @Override
    protected ServiceTestMetadata<PostgreSQLDockerService, Consumer<PostgreSQLDockerService>> provideFullyConfiguredServiceTestMetadata(ServiceConfigureContext context) {
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

        var service = PostgreSQLDockerServiceConfigurer
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
                .configure(context);

        var assertion = (Consumer<PostgreSQLDockerService>) actualService -> {
            var databaseAssertion = new SQLDBAssertionHelper(service);
            databaseAssertion.assertSingleEndpoint();
            databaseAssertion.asserDatabaseAccessible(schemaName);
            databaseAssertion.assertTablesCreated(schemaName, TABLES);
            databaseAssertion.assertRegisteredEndpointUnderProperty(endpointProperty);
            databaseAssertion.assertRegisteredEndpointUnderEnvironmentVariable(endpointEnvVariable);
            databaseAssertion.assertRegisteredUsernameUnderProperty(usernameProperty);
            databaseAssertion.assertRegisteredUsernameUnderEnvironmentVariable(usernameEnvVariable);
            databaseAssertion.assertRegisteredPasswordUnderProperty(passwordProperty);
            databaseAssertion.assertRegisteredPasswordUnderEnvironmentVariable(passwordEnvVariable);
        };

        return ServiceTestMetadata
                .<PostgreSQLDockerService, Consumer<PostgreSQLDockerService>>newInstance()
                .service(service)
                .assertion(assertion)
                .done();
    }

    @Override
    protected ServiceTestMetadata<PostgreSQLDockerService, BiConsumer<PostgreSQLDockerService, List<Endpoint>>> provideShutdownServiceTestMetadata(ServiceConfigureContext context) {
        var schemaName = "hussardb";
        var databaseSchema = SQLDatabaseSchema.scriptLess(schemaName);

        var service = PostgreSQLDockerServiceTestFactory.createMinimallyConfigured(databaseSchema, context);

        var assertion = (BiConsumer<PostgreSQLDockerService, List<Endpoint>>) (actualService, endpoints) -> {
            var endpoint = endpoints.getFirst();

            var databaseAssertion = new SQLDBAssertionHelper(service);
            databaseAssertion.assertDatabaseNotAccessible(schemaName, endpoint);
        };

        return ServiceTestMetadata
                .<PostgreSQLDockerService, BiConsumer<PostgreSQLDockerService, List<Endpoint>>>newInstance()
                .service(service)
                .assertion(assertion)
                .done();
    }

}
