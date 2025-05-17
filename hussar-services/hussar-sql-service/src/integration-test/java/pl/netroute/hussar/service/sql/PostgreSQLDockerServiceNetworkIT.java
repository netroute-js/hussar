package pl.netroute.hussar.service.sql;

import pl.netroute.hussar.core.service.BaseServiceNetworkIT;
import pl.netroute.hussar.core.service.ServiceConfigureContext;
import pl.netroute.hussar.service.sql.api.PostgreSQLDockerService;
import pl.netroute.hussar.service.sql.api.SQLDatabaseSchema;
import pl.netroute.hussar.service.sql.assertion.SQLDBAssertionHelper;

import java.util.function.Consumer;

class PostgreSQLDockerServiceNetworkIT extends BaseServiceNetworkIT<PostgreSQLDockerService> {

    @Override
    protected ServiceTestMetadata<PostgreSQLDockerService, Consumer<PostgreSQLDockerService>> provideEnableNetworkTestMetadata(ServiceConfigureContext context) {
        var schemaName = "hussardb";
        var databaseSchema = SQLDatabaseSchema.scriptLess(schemaName);

        var service = PostgreSQLDockerServiceTestFactory.createMinimallyConfigured(databaseSchema, context);

        var assertion = (Consumer<PostgreSQLDockerService>) actualService -> {
            var databaseAssertion = new SQLDBAssertionHelper(actualService);
            databaseAssertion.asserDatabaseAccessible(schemaName);
        };

        return ServiceTestMetadata
                .<PostgreSQLDockerService, Consumer<PostgreSQLDockerService>>newInstance()
                .service(service)
                .assertion(assertion)
                .done();
    }

    @Override
    protected ServiceTestMetadata<PostgreSQLDockerService, Consumer<PostgreSQLDockerService>> provideDisableNetworkTestMetadata(ServiceConfigureContext context) {
        var schemaName = "hussardb";
        var databaseSchema = SQLDatabaseSchema.scriptLess(schemaName);

        var service = PostgreSQLDockerServiceTestFactory.createMinimallyConfigured(databaseSchema, context);

        var assertion = (Consumer<PostgreSQLDockerService>) actualService -> {
            var databaseAssertion = new SQLDBAssertionHelper(actualService);
            databaseAssertion.assertDatabaseNotAccessible(schemaName);
        };

        return ServiceTestMetadata
                .<PostgreSQLDockerService, Consumer<PostgreSQLDockerService>>newInstance()
                .service(service)
                .assertion(assertion)
                .done();
    }

    @Override
    protected ServiceTestMetadata<PostgreSQLDockerService, Consumer<PostgreSQLDockerService>> provideIntroduceNetworkLatencyTestMetadata(ServiceConfigureContext context) {
        var schemaName = "hussardb";
        var databaseSchema = SQLDatabaseSchema.scriptLess(schemaName);

        var service = PostgreSQLDockerServiceTestFactory.createMinimallyConfigured(databaseSchema, context);

        var assertion = (Consumer<PostgreSQLDockerService>) actualService -> {
            var databaseAssertion = new SQLDBAssertionHelper(actualService);
            databaseAssertion.asserDatabaseAccessible(schemaName);
        };

        return ServiceTestMetadata
                .<PostgreSQLDockerService, Consumer<PostgreSQLDockerService>>newInstance()
                .service(service)
                .assertion(assertion)
                .done();
    }

    @Override
    protected ServiceTestMetadata<PostgreSQLDockerService, Consumer<PostgreSQLDockerService>> provideResetNetworkTestMetadata(ServiceConfigureContext context) {
        var schemaName = "hussardb";
        var databaseSchema = SQLDatabaseSchema.scriptLess(schemaName);

        var service = PostgreSQLDockerServiceTestFactory.createMinimallyConfigured(databaseSchema, context);

        var assertion = (Consumer<PostgreSQLDockerService>) actualService -> {
            var databaseAssertion = new SQLDBAssertionHelper(actualService);
            databaseAssertion.asserDatabaseAccessible(schemaName);
        };

        return ServiceTestMetadata
                .<PostgreSQLDockerService, Consumer<PostgreSQLDockerService>>newInstance()
                .service(service)
                .assertion(assertion)
                .done();
    }

}
