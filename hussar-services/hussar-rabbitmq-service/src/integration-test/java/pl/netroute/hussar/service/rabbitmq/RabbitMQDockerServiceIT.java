package pl.netroute.hussar.service.rabbitmq;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import pl.netroute.hussar.core.service.ServiceConfigureContext;
import pl.netroute.hussar.core.service.ServiceStartupContext;
import pl.netroute.hussar.service.rabbitmq.api.RabbitMQDockerService;
import pl.netroute.hussar.service.rabbitmq.api.RabbitMQDockerServiceConfigurer;
import pl.netroute.hussar.service.rabbitmq.api.RabbitMQQueue;
import pl.netroute.hussar.service.rabbitmq.assertion.RabbitMQAssertionHelper;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class RabbitMQDockerServiceIT {
    private static final String DOCKER_IMAGE_VERSION = "3.12.14-management-alpine";

    private static final boolean DURABLE = false;
    private static final boolean EXCLUSIVE = false;
    private static final boolean AUTO_DELETE = false;

    private RabbitMQDockerService rabbitMQService;

    @AfterEach
    public void cleanup() {
        Optional
                .ofNullable(rabbitMQService)
                .ifPresent(RabbitMQDockerService::shutdown);
    }

    @Test
    public void shouldStartRabbitMQService() {
        // given
        rabbitMQService = RabbitMQDockerServiceConfigurer
                .newInstance()
                .dockerImageVersion(DOCKER_IMAGE_VERSION)
                .done()
                .configure(ServiceConfigureContext.defaultContext());

        // when
        rabbitMQService.start(ServiceStartupContext.defaultContext());

        // then
        var rabbitMQAssertion = new RabbitMQAssertionHelper(rabbitMQService);
        rabbitMQAssertion.assertSingleEndpoint();
        rabbitMQAssertion.asserRabbitMQAccessible();
        rabbitMQAssertion.assertNoQueuesCreated();
        rabbitMQAssertion.assertNoEntriesRegistered();
    }

    @Test
    public void shouldStartExtendedRabbitMQService() {
        // given
        var name = "rabbitmq-instance";

        var queueA = createQueue("queueA");
        var queueB = createQueue("queueB");

        var endpointProperty = "rabbitmq.url";
        var endpointEnvVariable = "RABBITMQ_URL";

        var managementEndpointProperty = "rabbitmq.management.url";
        var managementEndpointEnvVariable = "RABBITMQ_MANAGEMENT_URL";

        var usernameProperty = "rabbitmq.username";
        var usernameEnvVariable = "RABBITMQ_USERNAME";

        var passwordProperty = "rabbitmq.password";
        var passwordEnvVariable = "RABBITMQ_PASSWORD";

        rabbitMQService = RabbitMQDockerServiceConfigurer
                .newInstance()
                .name(name)
                .dockerImageVersion(DOCKER_IMAGE_VERSION)
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
                .configure(ServiceConfigureContext.defaultContext());

        // when
        rabbitMQService.start(ServiceStartupContext.defaultContext());

        // then
        var rabbitMQAssertion = new RabbitMQAssertionHelper(rabbitMQService);
        rabbitMQAssertion.assertSingleEndpoint();
        rabbitMQAssertion.asserRabbitMQAccessible();
        rabbitMQAssertion.assertQueuesCreated(List.of(queueA, queueB));
        rabbitMQAssertion.assertRegisteredEndpointUnderProperty(endpointProperty);
        rabbitMQAssertion.assertRegisteredEndpointUnderEnvironmentVariable(endpointEnvVariable);
        rabbitMQAssertion.assertRegisteredManagementEndpointUnderProperty(managementEndpointProperty);
        rabbitMQAssertion.assertRegisteredManagementEndpointUnderEnvironmentVariable(managementEndpointEnvVariable);
        rabbitMQAssertion.assertRegisteredUsernameUnderProperty(usernameProperty);
        rabbitMQAssertion.assertRegisteredUsernameUnderEnvironmentVariable(usernameEnvVariable);
        rabbitMQAssertion.assertRegisteredPasswordUnderProperty(passwordProperty);
        rabbitMQAssertion.assertRegisteredPasswordUnderEnvironmentVariable(passwordEnvVariable);
    }

    @Test
    public void shouldShutdownRabbitMQService() {
        var name = "rabbitmq-instance";

        rabbitMQService = RabbitMQDockerServiceConfigurer
                .newInstance()
                .name(name)
                .dockerImageVersion(DOCKER_IMAGE_VERSION)
                .done()
                .configure(ServiceConfigureContext.defaultContext());

        // when
        rabbitMQService.start(ServiceStartupContext.defaultContext());

        var endpoint = rabbitMQService
                .getManagementEndpoint()
                .orElseThrow(() -> new IllegalStateException("Expected RabbitMQ ManagementAPI"));

        rabbitMQService.shutdown();

        // then
        var rabbitMQAssertion = new RabbitMQAssertionHelper(rabbitMQService);
        rabbitMQAssertion.asserRabbitMQNotAccessible(endpoint);
    }

    private RabbitMQQueue createQueue(String name) {
        return new RabbitMQQueue(name, DURABLE, EXCLUSIVE, AUTO_DELETE, Map.of());
    }

}
