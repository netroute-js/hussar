package pl.netroute.hussar.service.sql;

import pl.netroute.hussar.core.api.Endpoint;
import pl.netroute.hussar.core.service.BaseServiceIT;
import pl.netroute.hussar.core.service.ServiceConfigureContext;
import pl.netroute.hussar.service.sql.api.MySQLDockerService;
import pl.netroute.hussar.service.sql.api.MySQLDockerServiceConfigurer;
import pl.netroute.hussar.service.sql.api.SQLDatabaseSchema;
import pl.netroute.hussar.service.sql.assertion.SQLDBAssertionHelper;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

class MySQLDockerServiceIT extends BaseServiceIT<MySQLDockerService> {
    private static final List<String> TABLES = List.of("table_a", "table_b");

    @Override
    protected ServiceTestMetadata<MySQLDockerService, Consumer<MySQLDockerService>> provideMinimallyConfiguredServiceTestMetadata(ServiceConfigureContext context) {
        var schemaName = "hussardb";
        var databaseSchema = SQLDatabaseSchema.scriptLess(schemaName);

        var service = MySQLDockerServiceTestFactory.createMinimallyConfigured(databaseSchema, context);

        var assertion = (Consumer<MySQLDockerService>) actualService -> {
            var databaseAssertion = new SQLDBAssertionHelper(service);
            databaseAssertion.assertSingleEndpoint();
            databaseAssertion.asserDatabaseAccessible(schemaName);
            databaseAssertion.assertTablesNotCreated(schemaName, TABLES);
            databaseAssertion.assertNoEntriesRegistered();
        };

        return ServiceTestMetadata
                .<MySQLDockerService, Consumer<MySQLDockerService>>newInstance()
                .service(service)
                .assertion(assertion)
                .done();
    }

    @Override
    protected ServiceTestMetadata<MySQLDockerService, Consumer<MySQLDockerService>> provideFullyConfiguredServiceTestMetadata(ServiceConfigureContext context) {
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

        var service = MySQLDockerServiceConfigurer
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

        var assertion = (Consumer<MySQLDockerService>) actualService -> {
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
                .<MySQLDockerService, Consumer<MySQLDockerService>>newInstance()
                .service(service)
                .assertion(assertion)
                .done();
    }

    @Override
    protected ServiceTestMetadata<MySQLDockerService, BiConsumer<MySQLDockerService, List<Endpoint>>> provideShutdownServiceTestMetadata(ServiceConfigureContext context) {
        var schemaName = "hussardb";
        var databaseSchema = SQLDatabaseSchema.scriptLess(schemaName);

        var service = MySQLDockerServiceTestFactory.createMinimallyConfigured(databaseSchema, context);

        var assertion = (BiConsumer<MySQLDockerService, List<Endpoint>>) (actualService, endpoints) -> {
            var endpoint = endpoints.getFirst();

            var databaseAssertion = new SQLDBAssertionHelper(service);
            databaseAssertion.assertDatabaseNotAccessible(schemaName, endpoint);
        };

        return ServiceTestMetadata
                .<MySQLDockerService, BiConsumer<MySQLDockerService, List<Endpoint>>>newInstance()
                .service(service)
                .assertion(assertion)
                .done();
    }

}
