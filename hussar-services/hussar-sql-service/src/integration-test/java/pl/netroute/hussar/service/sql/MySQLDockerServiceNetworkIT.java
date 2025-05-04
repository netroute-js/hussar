package pl.netroute.hussar.service.sql;

import pl.netroute.hussar.core.service.BaseServiceNetworkIT;
import pl.netroute.hussar.core.service.ServiceConfigureContext;
import pl.netroute.hussar.service.sql.api.MySQLDockerService;
import pl.netroute.hussar.service.sql.api.SQLDatabaseSchema;
import pl.netroute.hussar.service.sql.assertion.SQLDBAssertionHelper;

import java.util.function.Consumer;

class MySQLDockerServiceNetworkIT extends BaseServiceNetworkIT<MySQLDockerService> {

    @Override
    protected ServiceTestMetadata<MySQLDockerService, Consumer<MySQLDockerService>> provideEnableNetworkTestMetadata(ServiceConfigureContext context) {
        var schemaName = "hussardb";
        var databaseSchema = SQLDatabaseSchema.scriptLess(schemaName);

        var service = MySQLDockerServiceTestFactory.createMinimallyConfigured(databaseSchema, context);

        var assertion = (Consumer<MySQLDockerService>) actualService -> {
            var databaseAssertion = new SQLDBAssertionHelper(actualService);
            databaseAssertion.asserDatabaseAccessible(schemaName);
        };

        return ServiceTestMetadata
                .<MySQLDockerService, Consumer<MySQLDockerService>>newInstance()
                .service(service)
                .assertion(assertion)
                .done();
    }

    @Override
    protected ServiceTestMetadata<MySQLDockerService, Consumer<MySQLDockerService>> provideDisableNetworkTestMetadata(ServiceConfigureContext context) {
        var schemaName = "hussardb";
        var databaseSchema = SQLDatabaseSchema.scriptLess(schemaName);

        var service = MySQLDockerServiceTestFactory.createMinimallyConfigured(databaseSchema, context);

        var assertion = (Consumer<MySQLDockerService>) actualService -> {
            var databaseAssertion = new SQLDBAssertionHelper(actualService);
            databaseAssertion.assertDatabaseNotAccessible(schemaName);
        };

        return ServiceTestMetadata
                .<MySQLDockerService, Consumer<MySQLDockerService>>newInstance()
                .service(service)
                .assertion(assertion)
                .done();
    }

    @Override
    protected ServiceTestMetadata<MySQLDockerService, Consumer<MySQLDockerService>> provideIntroduceNetworkLatencyTestMetadata(ServiceConfigureContext context) {
        var schemaName = "hussardb";
        var databaseSchema = SQLDatabaseSchema.scriptLess(schemaName);

        var service = MySQLDockerServiceTestFactory.createMinimallyConfigured(databaseSchema, context);

        var assertion = (Consumer<MySQLDockerService>) actualService -> {
            var databaseAssertion = new SQLDBAssertionHelper(actualService);
            databaseAssertion.asserDatabaseAccessible(schemaName);
        };

        return ServiceTestMetadata
                .<MySQLDockerService, Consumer<MySQLDockerService>>newInstance()
                .service(service)
                .assertion(assertion)
                .done();
    }

    @Override
    protected ServiceTestMetadata<MySQLDockerService, Consumer<MySQLDockerService>> provideResetNetworkTestMetadata(ServiceConfigureContext context) {
        var schemaName = "hussardb";
        var databaseSchema = SQLDatabaseSchema.scriptLess(schemaName);

        var service = MySQLDockerServiceTestFactory.createMinimallyConfigured(databaseSchema, context);

        var assertion = (Consumer<MySQLDockerService>) actualService -> {
            var databaseAssertion = new SQLDBAssertionHelper(actualService);
            databaseAssertion.asserDatabaseAccessible(schemaName);
        };

        return ServiceTestMetadata
                .<MySQLDockerService, Consumer<MySQLDockerService>>newInstance()
                .service(service)
                .assertion(assertion)
                .done();
    }

}
