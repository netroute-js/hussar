package pl.netroute.hussar.service.sql;

import pl.netroute.hussar.core.api.Endpoint;
import pl.netroute.hussar.core.service.BaseServiceIT;
import pl.netroute.hussar.core.service.ServiceConfigureContext;
import pl.netroute.hussar.service.sql.api.MariaDBDockerService;
import pl.netroute.hussar.service.sql.api.MariaDBDockerServiceConfigurer;
import pl.netroute.hussar.service.sql.api.SQLDatabaseSchema;
import pl.netroute.hussar.service.sql.assertion.SQLDBAssertionHelper;

import java.time.Duration;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

class MariaDBDockerServiceIT extends BaseServiceIT<MariaDBDockerService> {
    private static final List<String> TABLES = List.of("table_a", "table_b");

    @Override
    protected ServiceTestMetadata<MariaDBDockerService, Consumer<MariaDBDockerService>> provideMinimallyConfiguredServiceTestMetadata(ServiceConfigureContext context) {
        var schemaName = "hussardb";
        var databaseSchema = SQLDatabaseSchema.scriptLess(schemaName);

        var service = MariaDBDockerServiceTestFactory.createMinimallyConfigured(databaseSchema, context);

        var assertion = (Consumer<MariaDBDockerService>) actualService -> {
            var databaseAssertion = new SQLDBAssertionHelper(service);
            databaseAssertion.assertSingleEndpoint();
            databaseAssertion.asserDatabaseAccessible(schemaName);
            databaseAssertion.assertTablesNotCreated(schemaName, TABLES);
            databaseAssertion.assertNoEntriesRegistered();
        };

        return ServiceTestMetadata
                .<MariaDBDockerService, Consumer<MariaDBDockerService>>newInstance()
                .service(service)
                .assertion(assertion)
                .done();
    }

    @Override
    protected ServiceTestMetadata<MariaDBDockerService, Consumer<MariaDBDockerService>> provideFullyConfiguredServiceTestMetadata(ServiceConfigureContext context) {
        var name = "mariadb-instance";
        var dockerVersion = "10.5.23";

        var startupTimeout = Duration.ofSeconds(100L);

        var endpointProperty = "mariadb.url";
        var endpointEnvVariable = "MARIADB_URL";

        var usernameProperty = "mariadb.username";
        var usernameEnvVariable = "MARIADB_USERNAME";

        var passwordProperty = "mariadb.password";
        var passwordEnvVariable = "MARIADB_PASSWORD";

        var schemaName = "hussardb";
        var scriptsLocation = "/flyway/scripts";
        var databaseSchema = new SQLDatabaseSchema(schemaName, scriptsLocation);

        var service = MariaDBDockerServiceConfigurer
                .newInstance()
                .name(name)
                .dockerImageVersion(dockerVersion)
                .startupTimeout(startupTimeout)
                .databaseSchema(databaseSchema)
                .registerEndpointUnderProperty(endpointProperty)
                .registerEndpointUnderEnvironmentVariable(endpointEnvVariable)
                .registerUsernameUnderProperty(usernameProperty)
                .registerUsernameUnderEnvironmentVariable(usernameEnvVariable)
                .registerPasswordUnderProperty(passwordProperty)
                .registerPasswordUnderEnvironmentVariable(passwordEnvVariable)
                .done()
                .configure(context);

        var assertion = (Consumer<MariaDBDockerService>) actualService -> {
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
                .<MariaDBDockerService, Consumer<MariaDBDockerService>>newInstance()
                .service(service)
                .assertion(assertion)
                .done();
    }

    @Override
    protected ServiceTestMetadata<MariaDBDockerService, BiConsumer<MariaDBDockerService, List<Endpoint>>> provideShutdownServiceTestMetadata(ServiceConfigureContext context) {
        var schemaName = "hussardb";
        var databaseSchema = SQLDatabaseSchema.scriptLess(schemaName);

        var service = MariaDBDockerServiceTestFactory.createMinimallyConfigured(databaseSchema, context);

        var assertion = (BiConsumer<MariaDBDockerService, List<Endpoint>>) (actualService, endpoints) -> {
            var endpoint = endpoints.getFirst();

            var databaseAssertion = new SQLDBAssertionHelper(service);
            databaseAssertion.assertDatabaseNotAccessible(schemaName, endpoint);
        };

        return ServiceTestMetadata
                .<MariaDBDockerService, BiConsumer<MariaDBDockerService, List<Endpoint>>>newInstance()
                .service(service)
                .assertion(assertion)
                .done();
    }

}
