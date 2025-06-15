package pl.netroute.hussar.service.rabbitmq;

import pl.netroute.hussar.core.api.Endpoint;
import pl.netroute.hussar.core.service.BaseServiceIT;
import pl.netroute.hussar.core.service.ServiceConfigureContext;
import pl.netroute.hussar.service.rabbitmq.api.RabbitMQDockerService;
import pl.netroute.hussar.service.rabbitmq.api.RabbitMQDockerServiceConfigurer;
import pl.netroute.hussar.service.rabbitmq.api.RabbitMQQueue;
import pl.netroute.hussar.service.rabbitmq.assertion.RabbitMQAssertionHelper;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class RabbitMQDockerServiceIT extends BaseServiceIT<RabbitMQDockerService> {
    private static final String DOCKER_IMAGE_VERSION = "3.12.14-management-alpine";

    private static final boolean DURABLE = false;
    private static final boolean EXCLUSIVE = false;
    private static final boolean AUTO_DELETE = false;

    @Override
    protected ServiceTestMetadata<RabbitMQDockerService, Consumer<RabbitMQDockerService>> provideMinimallyConfiguredServiceTestMetadata(ServiceConfigureContext context) {
        var service = RabbitMQDockerServiceTestFactory.createMinimallyConfigured(DOCKER_IMAGE_VERSION, context);

        var assertion = (Consumer<RabbitMQDockerService>) actualService -> {
            var rabbitMQAssertion = new RabbitMQAssertionHelper(actualService);
            rabbitMQAssertion.assertSingleEndpoint();
            rabbitMQAssertion.asserRabbitMQAccessible();
            rabbitMQAssertion.assertRabbitMQManagementApiAccessible();
            rabbitMQAssertion.assertNoQueuesCreated();
            rabbitMQAssertion.assertNoEntriesRegistered();
        };

        return ServiceTestMetadata
                .<RabbitMQDockerService, Consumer<RabbitMQDockerService>>newInstance()
                .service(service)
                .assertion(assertion)
                .done();
    }

    @Override
    protected ServiceTestMetadata<RabbitMQDockerService, Consumer<RabbitMQDockerService>> provideFullyConfiguredServiceTestMetadata(ServiceConfigureContext context) {
        var name = "rabbitmq-instance";

        var queueA = createQueue("queueA");
        var queueB = createQueue("queueB");

        var startupTimeout = Duration.ofSeconds(100L);

        var endpointProperty = "rabbitmq.url";
        var endpointEnvVariable = "RABBITMQ_URL";

        var managementEndpointProperty = "rabbitmq.management.url";
        var managementEndpointEnvVariable = "RABBITMQ_MANAGEMENT_URL";

        var usernameProperty = "rabbitmq.username";
        var usernameEnvVariable = "RABBITMQ_USERNAME";

        var passwordProperty = "rabbitmq.password";
        var passwordEnvVariable = "RABBITMQ_PASSWORD";

        var service = RabbitMQDockerServiceConfigurer
                .newInstance()
                .name(name)
                .dockerImageVersion(DOCKER_IMAGE_VERSION)
                .startupTimeout(startupTimeout)
                .queue(queueA)
                .queue(queueB)
                .registerEndpointUnderProperty(endpointProperty)
                .registerEndpointUnderEnvironmentVariable(endpointEnvVariable)
                .registerUsernameUnderProperty(usernameProperty)
                .registerUsernameUnderEnvironmentVariable(usernameEnvVariable)
                .registerPasswordUnderProperty(passwordProperty)
                .registerPasswordUnderEnvironmentVariable(passwordEnvVariable)
                .registerManagementEndpointUnderProperty(managementEndpointProperty)
                .registerManagementEndpointUnderEnvironmentVariable(managementEndpointEnvVariable)
                .done()
                .configure(context);

        var assertion = (Consumer<RabbitMQDockerService>) actualService -> {
            var rabbitMQAssertion = new RabbitMQAssertionHelper(actualService);
            rabbitMQAssertion.assertSingleEndpoint();
            rabbitMQAssertion.asserRabbitMQAccessible();
            rabbitMQAssertion.assertRabbitMQManagementApiAccessible();
            rabbitMQAssertion.assertQueuesCreated(List.of(queueA, queueB));
            rabbitMQAssertion.assertRegisteredEndpointUnderProperty(endpointProperty);
            rabbitMQAssertion.assertRegisteredEndpointUnderEnvironmentVariable(endpointEnvVariable);
            rabbitMQAssertion.assertRegisteredManagementEndpointUnderProperty(managementEndpointProperty);
            rabbitMQAssertion.assertRegisteredManagementEndpointUnderEnvironmentVariable(managementEndpointEnvVariable);
            rabbitMQAssertion.assertRegisteredUsernameUnderProperty(usernameProperty);
            rabbitMQAssertion.assertRegisteredUsernameUnderEnvironmentVariable(usernameEnvVariable);
            rabbitMQAssertion.assertRegisteredPasswordUnderProperty(passwordProperty);
            rabbitMQAssertion.assertRegisteredPasswordUnderEnvironmentVariable(passwordEnvVariable);
        };

        return ServiceTestMetadata
                .<RabbitMQDockerService, Consumer<RabbitMQDockerService>>newInstance()
                .service(service)
                .assertion(assertion)
                .done();
    }

    @Override
    protected ServiceTestMetadata<RabbitMQDockerService, BiConsumer<RabbitMQDockerService, List<Endpoint>>> provideShutdownServiceTestMetadata(ServiceConfigureContext context) {
        var service = RabbitMQDockerServiceTestFactory.createMinimallyConfigured(DOCKER_IMAGE_VERSION, context);

        var assertion = (BiConsumer<RabbitMQDockerService, List<Endpoint>>) (actualService, endpoints) -> {
            var endpoint = endpoints.getFirst();

            var rabbitMQAssertion = new RabbitMQAssertionHelper(actualService);
            rabbitMQAssertion.asserRabbitMQNotAccessible(endpoint);
        };

        return ServiceTestMetadata
                .<RabbitMQDockerService, BiConsumer<RabbitMQDockerService, List<Endpoint>>>newInstance()
                .service(service)
                .assertion(assertion)
                .done();
    }

    private RabbitMQQueue createQueue(String name) {
        return new RabbitMQQueue(name, DURABLE, EXCLUSIVE, AUTO_DELETE, Map.of());
    }

}
