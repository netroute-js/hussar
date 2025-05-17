package pl.netroute.hussar.service.rabbitmq;

import pl.netroute.hussar.core.service.BaseServiceNetworkIT;
import pl.netroute.hussar.core.service.ServiceConfigureContext;
import pl.netroute.hussar.service.rabbitmq.api.RabbitMQDockerService;
import pl.netroute.hussar.service.rabbitmq.assertion.RabbitMQAssertionHelper;

import java.util.function.Consumer;

class RabbitMQDockerServiceNetworkIT extends BaseServiceNetworkIT<RabbitMQDockerService> {
    private static final String DOCKER_IMAGE_VERSION = "3.12.14-management-alpine";

    @Override
    protected ServiceTestMetadata<RabbitMQDockerService, Consumer<RabbitMQDockerService>> provideEnableNetworkTestMetadata(ServiceConfigureContext context) {
        var service = RabbitMQDockerServiceTestFactory.createMinimallyConfigured(DOCKER_IMAGE_VERSION, context);

        var assertion = (Consumer<RabbitMQDockerService>) actualService -> {
            var rabbitMQAssertion = new RabbitMQAssertionHelper(actualService);
            rabbitMQAssertion.asserRabbitMQAccessible();
        };

        return ServiceTestMetadata
                .<RabbitMQDockerService, Consumer<RabbitMQDockerService>>newInstance()
                .service(service)
                .assertion(assertion)
                .done();
    }

    @Override
    protected ServiceTestMetadata<RabbitMQDockerService, Consumer<RabbitMQDockerService>> provideDisableNetworkTestMetadata(ServiceConfigureContext context) {
        var service = RabbitMQDockerServiceTestFactory.createMinimallyConfigured(DOCKER_IMAGE_VERSION, context);

        var assertion = (Consumer<RabbitMQDockerService>) actualService -> {
            var rabbitMQAssertion = new RabbitMQAssertionHelper(actualService);
            rabbitMQAssertion.asserRabbitMQNotAccessible();
        };

        return ServiceTestMetadata
                .<RabbitMQDockerService, Consumer<RabbitMQDockerService>>newInstance()
                .service(service)
                .assertion(assertion)
                .done();
    }

    @Override
    protected ServiceTestMetadata<RabbitMQDockerService, Consumer<RabbitMQDockerService>> provideIntroduceNetworkLatencyTestMetadata(ServiceConfigureContext context) {
        var service = RabbitMQDockerServiceTestFactory.createMinimallyConfigured(DOCKER_IMAGE_VERSION, context);

        var assertion = (Consumer<RabbitMQDockerService>) actualService -> {
            var rabbitMQAssertion = new RabbitMQAssertionHelper(actualService);
            rabbitMQAssertion.asserRabbitMQAccessible();
        };

        return ServiceTestMetadata
                .<RabbitMQDockerService, Consumer<RabbitMQDockerService>>newInstance()
                .service(service)
                .assertion(assertion)
                .done();
    }

    @Override
    protected ServiceTestMetadata<RabbitMQDockerService, Consumer<RabbitMQDockerService>> provideResetNetworkTestMetadata(ServiceConfigureContext context) {
        var service = RabbitMQDockerServiceTestFactory.createMinimallyConfigured(DOCKER_IMAGE_VERSION, context);

        var assertion = (Consumer<RabbitMQDockerService>) actualService -> {
            var rabbitMQAssertion = new RabbitMQAssertionHelper(actualService);
            rabbitMQAssertion.asserRabbitMQAccessible();
        };

        return ServiceTestMetadata
                .<RabbitMQDockerService, Consumer<RabbitMQDockerService>>newInstance()
                .service(service)
                .assertion(assertion)
                .done();
    }

}
