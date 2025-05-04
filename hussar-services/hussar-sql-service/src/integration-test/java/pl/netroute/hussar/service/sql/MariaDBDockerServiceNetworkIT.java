package pl.netroute.hussar.service.sql;

import pl.netroute.hussar.core.service.BaseServiceNetworkIT;
import pl.netroute.hussar.core.service.ServiceConfigureContext;
import pl.netroute.hussar.service.sql.api.MariaDBDockerService;
import pl.netroute.hussar.service.sql.api.SQLDatabaseSchema;
import pl.netroute.hussar.service.sql.assertion.SQLDBAssertionHelper;

import java.util.function.Consumer;

class MariaDBDockerServiceNetworkIT extends BaseServiceNetworkIT<MariaDBDockerService> {

    @Override
    protected ServiceTestMetadata<MariaDBDockerService, Consumer<MariaDBDockerService>> provideEnableNetworkTestMetadata(ServiceConfigureContext context) {
        var schemaName = "hussardb";
        var databaseSchema = SQLDatabaseSchema.scriptLess(schemaName);

        var service = MariaDBDockerServiceTestFactory.createMinimallyConfigured(databaseSchema, context);

        var assertion = (Consumer<MariaDBDockerService>) actualService -> {
            var databaseAssertion = new SQLDBAssertionHelper(actualService);
            databaseAssertion.asserDatabaseAccessible(schemaName);
        };

        return ServiceTestMetadata
                .<MariaDBDockerService, Consumer<MariaDBDockerService>>newInstance()
                .service(service)
                .assertion(assertion)
                .done();
    }

    @Override
    protected ServiceTestMetadata<MariaDBDockerService, Consumer<MariaDBDockerService>> provideDisableNetworkTestMetadata(ServiceConfigureContext context) {
        var schemaName = "hussardb";
        var databaseSchema = SQLDatabaseSchema.scriptLess(schemaName);

        var service = MariaDBDockerServiceTestFactory.createMinimallyConfigured(databaseSchema, context);

        var assertion = (Consumer<MariaDBDockerService>) actualService -> {
            var databaseAssertion = new SQLDBAssertionHelper(actualService);
            databaseAssertion.assertDatabaseNotAccessible(schemaName);
        };

        return ServiceTestMetadata
                .<MariaDBDockerService, Consumer<MariaDBDockerService>>newInstance()
                .service(service)
                .assertion(assertion)
                .done();
    }

    @Override
    protected ServiceTestMetadata<MariaDBDockerService, Consumer<MariaDBDockerService>> provideIntroduceNetworkLatencyTestMetadata(ServiceConfigureContext context) {
        var schemaName = "hussardb";
        var databaseSchema = SQLDatabaseSchema.scriptLess(schemaName);

        var service = MariaDBDockerServiceTestFactory.createMinimallyConfigured(databaseSchema, context);

        var assertion = (Consumer<MariaDBDockerService>) actualService -> {
            var databaseAssertion = new SQLDBAssertionHelper(actualService);
            databaseAssertion.asserDatabaseAccessible(schemaName);
        };

        return ServiceTestMetadata
                .<MariaDBDockerService, Consumer<MariaDBDockerService>>newInstance()
                .service(service)
                .assertion(assertion)
                .done();
    }

    @Override
    protected ServiceTestMetadata<MariaDBDockerService, Consumer<MariaDBDockerService>> provideResetNetworkTestMetadata(ServiceConfigureContext context) {
        var schemaName = "hussardb";
        var databaseSchema = SQLDatabaseSchema.scriptLess(schemaName);

        var service = MariaDBDockerServiceTestFactory.createMinimallyConfigured(databaseSchema, context);

        var assertion = (Consumer<MariaDBDockerService>) actualService -> {
            var databaseAssertion = new SQLDBAssertionHelper(actualService);
            databaseAssertion.asserDatabaseAccessible(schemaName);
        };

        return ServiceTestMetadata
                .<MariaDBDockerService, Consumer<MariaDBDockerService>>newInstance()
                .service(service)
                .assertion(assertion)
                .done();
    }

}
