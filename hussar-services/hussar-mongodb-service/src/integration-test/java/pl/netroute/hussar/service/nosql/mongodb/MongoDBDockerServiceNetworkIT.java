package pl.netroute.hussar.service.nosql.mongodb;

import pl.netroute.hussar.core.service.BaseServiceNetworkIT;
import pl.netroute.hussar.core.service.ServiceConfigureContext;
import pl.netroute.hussar.service.nosql.mongodb.api.MongoDBDockerService;
import pl.netroute.hussar.service.nosql.mongodb.assertion.MongoDBAssertionHelper;

import java.util.function.Consumer;

class MongoDBDockerServiceNetworkIT extends BaseServiceNetworkIT<MongoDBDockerService> {

    @Override
    protected ServiceTestMetadata<MongoDBDockerService, Consumer<MongoDBDockerService>> provideEnableNetworkTestMetadata(ServiceConfigureContext context) {
        var service = MongoDBDockerServiceTestFactory.createMinimallyConfigured(context);

        var assertion = (Consumer<MongoDBDockerService>) actualService -> {
            var mongoAssertion = new MongoDBAssertionHelper(actualService);
            mongoAssertion.asserMongoDBAccessible();
        };

        return ServiceTestMetadata
                .<MongoDBDockerService, Consumer<MongoDBDockerService>>newInstance()
                .service(service)
                .assertion(assertion)
                .done();
    }

    @Override
    protected ServiceTestMetadata<MongoDBDockerService, Consumer<MongoDBDockerService>> provideDisableNetworkTestMetadata(ServiceConfigureContext context) {
        var service = MongoDBDockerServiceTestFactory.createMinimallyConfigured(context);

        var assertion = (Consumer<MongoDBDockerService>) actualService -> {
            var mongoAssertion = new MongoDBAssertionHelper(actualService);
            mongoAssertion.assertMongoDBNotAccessible();
        };

        return ServiceTestMetadata
                .<MongoDBDockerService, Consumer<MongoDBDockerService>>newInstance()
                .service(service)
                .assertion(assertion)
                .done();
    }

    @Override
    protected ServiceTestMetadata<MongoDBDockerService, Consumer<MongoDBDockerService>> provideIntroduceNetworkLatencyTestMetadata(ServiceConfigureContext context) {
        var service = MongoDBDockerServiceTestFactory.createMinimallyConfigured(context);

        var assertion = (Consumer<MongoDBDockerService>) actualService -> {
            var mongoAssertion = new MongoDBAssertionHelper(actualService);
            mongoAssertion.asserMongoDBAccessible();
        };

        return ServiceTestMetadata
                .<MongoDBDockerService, Consumer<MongoDBDockerService>>newInstance()
                .service(service)
                .assertion(assertion)
                .done();
    }

    @Override
    protected ServiceTestMetadata<MongoDBDockerService, Consumer<MongoDBDockerService>> provideResetNetworkTestMetadata(ServiceConfigureContext context) {
        var service = MongoDBDockerServiceTestFactory.createMinimallyConfigured(context);

        var assertion = (Consumer<MongoDBDockerService>) actualService -> {
            var mongoAssertion = new MongoDBAssertionHelper(actualService);
            mongoAssertion.asserMongoDBAccessible();
        };

        return ServiceTestMetadata
                .<MongoDBDockerService, Consumer<MongoDBDockerService>>newInstance()
                .service(service)
                .assertion(assertion)
                .done();
    }

}
