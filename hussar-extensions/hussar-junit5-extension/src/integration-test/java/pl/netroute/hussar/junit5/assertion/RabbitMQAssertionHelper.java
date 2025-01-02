package pl.netroute.hussar.junit5.assertion;

import com.rabbitmq.http.client.Client;
import com.rabbitmq.http.client.ClientParameters;
import com.rabbitmq.http.client.domain.QueueInfo;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import pl.netroute.hussar.core.api.Endpoint;
import pl.netroute.hussar.core.application.api.Application;
import pl.netroute.hussar.core.helper.EndpointHelper;
import pl.netroute.hussar.junit5.helper.ApplicationClientRunner;
import pl.netroute.hussar.service.rabbitmq.RabbitMQDockerService;
import pl.netroute.hussar.service.rabbitmq.api.RabbitMQCredentials;
import pl.netroute.hussar.service.rabbitmq.api.RabbitMQQueue;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.netroute.hussar.junit5.assertion.ApplicationPropertiesAssertionHelper.assertPropertyConfigured;
import static pl.netroute.hussar.junit5.config.ApplicationProperties.RABBITMQ_ALTERNATIVE_PASSWORD_PROPERTY;
import static pl.netroute.hussar.junit5.config.ApplicationProperties.RABBITMQ_ALTERNATIVE_URL_PROPERTY;
import static pl.netroute.hussar.junit5.config.ApplicationProperties.RABBITMQ_ALTERNATIVE_USERNAME_PROPERTY;
import static pl.netroute.hussar.junit5.config.ApplicationProperties.RABBITMQ_PASSWORD_PROPERTY;
import static pl.netroute.hussar.junit5.config.ApplicationProperties.RABBITMQ_URL_PROPERTY;
import static pl.netroute.hussar.junit5.config.ApplicationProperties.RABBITMQ_USERNAME_PROPERTY;
import static pl.netroute.hussar.junit5.factory.RabbitMQServiceFactory.RABBITMQ_EVENTS_QUEUE;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RabbitMQAssertionHelper {
    private static final String MANAGEMENT_API_PREFIX_PATH = "/api";

    public static void assertRabbitMQBootstrapped(@NonNull RabbitMQDockerService rabbitMQService,
                                                  @NonNull Application application) {
        var endpoint = EndpointHelper.getAnyEndpointOrFail(rabbitMQService);
        var managementEndpoint = rabbitMQService
                .getManagementEndpoint()
                .orElseThrow(() -> new IllegalStateException("Expected RabbitMQ Management API to be present"));
        var credentials = rabbitMQService.getCredentials();
        var applicationClientRunner = new ApplicationClientRunner(application);

        assertRabbitMQReachable(managementEndpoint, credentials);
        assertQueuesCreated(managementEndpoint, credentials, List.of(RABBITMQ_EVENTS_QUEUE));
        applicationClientRunner.run(applicationClient -> assertPropertyConfigured(RABBITMQ_URL_PROPERTY, endpoint.address(), applicationClient));
        applicationClientRunner.run(applicationClient -> assertPropertyConfigured(RABBITMQ_ALTERNATIVE_URL_PROPERTY, endpoint.address(), applicationClient));
        applicationClientRunner.run(applicationClient -> assertPropertyConfigured(RABBITMQ_USERNAME_PROPERTY, credentials.username(), applicationClient));
        applicationClientRunner.run(applicationClient -> assertPropertyConfigured(RABBITMQ_ALTERNATIVE_USERNAME_PROPERTY, credentials.username(), applicationClient));
        applicationClientRunner.run(applicationClient -> assertPropertyConfigured(RABBITMQ_PASSWORD_PROPERTY, credentials.password(), applicationClient));
        applicationClientRunner.run(applicationClient -> assertPropertyConfigured(RABBITMQ_ALTERNATIVE_PASSWORD_PROPERTY, credentials.password(), applicationClient));
    }

    private static void assertRabbitMQReachable(Endpoint endpoint, RabbitMQCredentials credentials) {
        var client = createClient(endpoint, credentials);

        var currentUser = client.whoAmI();
        assertThat(currentUser.getName()).isEqualTo(credentials.username());
    }

    private static void assertQueuesCreated(Endpoint endpoint,
                                            RabbitMQCredentials credentials,
                                            List<RabbitMQQueue> queues) {
        var client = createClient(endpoint, credentials);

        var actualQueueNames = client
                .getQueues()
                .stream()
                .map(QueueInfo::getName)
                .toList();

        var expectedQueueNames = queues
                .stream()
                .map(RabbitMQQueue::name)
                .toList();

        assertThat(actualQueueNames).containsExactlyInAnyOrderElementsOf(expectedQueueNames);
    }

    private static Client createClient(Endpoint endpoint, RabbitMQCredentials credentials) {
        try {
            var connectionParameters = new ClientParameters()
                    .url(endpoint.address() + MANAGEMENT_API_PREFIX_PATH)
                    .username(credentials.username())
                    .password(credentials.password());

            return new Client(connectionParameters);
        } catch (Exception ex) {
            throw new IllegalStateException("Could not create RabbitMQ client", ex);
        }
    }
}
