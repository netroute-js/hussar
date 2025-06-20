package pl.netroute.hussar.service.rabbitmq.assertion;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.http.client.Client;
import com.rabbitmq.http.client.ClientParameters;
import com.rabbitmq.http.client.domain.QueueInfo;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import pl.netroute.hussar.core.api.Endpoint;
import pl.netroute.hussar.core.configuration.api.ConfigurationEntry;
import pl.netroute.hussar.core.configuration.api.EnvVariableConfigurationEntry;
import pl.netroute.hussar.core.configuration.api.PropertyConfigurationEntry;
import pl.netroute.hussar.core.helper.EndpointHelper;
import pl.netroute.hussar.service.rabbitmq.api.RabbitMQDockerService;
import pl.netroute.hussar.service.rabbitmq.api.RabbitMQQueue;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor
public class RabbitMQAssertionHelper {
    private static final int SINGLE = 1;

    private static final Duration CONNECTION_TIMEOUT = Duration.ofSeconds(4L);

    private static final String MANAGEMENT_API_PREFIX_PATH = "/api";

    private final RabbitMQDockerService rabbitMQ;

    public void assertSingleEndpoint() {
        assertThat(rabbitMQ.getEndpoints()).hasSize(SINGLE);
    }

    public void asserRabbitMQAccessible() {
        var endpoint = EndpointHelper.getAnyEndpointOrFail(rabbitMQ);
        var directEndpoint = EndpointHelper.getAnyDirectEndpointOrFail(rabbitMQ);

        assertRabbitMQAccessibility(endpoint);
        assertRabbitMQAccessibility(directEndpoint);
    }

    public void asserRabbitMQNotAccessible() {
        var endpoint = EndpointHelper.getAnyEndpointOrFail(rabbitMQ);

        asserRabbitMQNotAccessible(endpoint);
    }

    public void asserRabbitMQNotAccessible(@NonNull Endpoint endpoint) {
        var connectionFactory = createConnectionFactory(endpoint);

        try(var connection = connectionFactory.newConnection()) {
        } catch (Exception ex) {
        }
    }

    public void assertRabbitMQManagementApiAccessible() {
        var endpoint = rabbitMQ
                .getManagementEndpoint()
                .orElseThrow(() -> new IllegalStateException("Expected RabbitMQ ManagementAPI"));

        var client = createManagementClient(endpoint);
        var credentials = rabbitMQ.getCredentials();

        var currentUser = client.whoAmI();
        assertThat(currentUser.getName()).isEqualTo(credentials.username());
    }

    public void assertQueuesCreated(@NonNull List<RabbitMQQueue> queues) {
        var endpoint = rabbitMQ
                .getManagementEndpoint()
                .orElseThrow(() -> new IllegalStateException("Expected RabbitMQ ManagementAPI"));

        var client = createManagementClient(endpoint);

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

    public void assertNoQueuesCreated() {
        var endpoint = rabbitMQ
                .getManagementEndpoint()
                .orElseThrow(() -> new IllegalStateException("Expected RabbitMQ ManagementAPI"));

        var client = createManagementClient(endpoint);

        var queues = client.getQueues();
        assertThat(queues).isEmpty();
    }


    public void assertRegisteredEndpointUnderProperty(@NonNull String registeredProperty) {
        var endpoint = EndpointHelper.getAnyEndpointOrFail(rabbitMQ);

        assertRegisteredEntryInConfigRegistry(registeredProperty, endpoint.address(), PropertyConfigurationEntry.class);
    }

    public void assertRegisteredManagementEndpointUnderProperty(@NonNull String registeredProperty) {
        var endpoint = rabbitMQ
                .getManagementEndpoint()
                .orElseThrow(() -> new IllegalStateException("Expected RabbitMQ ManagementAPI"));

        assertRegisteredEntryInConfigRegistry(registeredProperty, endpoint.address(), PropertyConfigurationEntry.class);
    }

    public void assertRegisteredEndpointUnderEnvironmentVariable(@NonNull String registeredEnvVariable) {
        var endpoint = EndpointHelper.getAnyEndpointOrFail(rabbitMQ);

        assertRegisteredEntryInConfigRegistry(registeredEnvVariable, endpoint.address(), EnvVariableConfigurationEntry.class);
    }

    public void assertRegisteredManagementEndpointUnderEnvironmentVariable(@NonNull String registeredEnvVariable) {
        var endpoint = rabbitMQ
                .getManagementEndpoint()
                .orElseThrow(() -> new IllegalStateException("Expected RabbitMQ ManagementAPI"));

        assertRegisteredEntryInConfigRegistry(registeredEnvVariable, endpoint.address(), EnvVariableConfigurationEntry.class);
    }

    public void assertRegisteredUsernameUnderProperty(@NonNull String registeredProperty) {
        var credentials = rabbitMQ.getCredentials();

        assertRegisteredEntryInConfigRegistry(registeredProperty, credentials.username(), PropertyConfigurationEntry.class);
    }

    public void assertRegisteredUsernameUnderEnvironmentVariable(@NonNull String registeredEnvVariable) {
        var credentials = rabbitMQ.getCredentials();

        assertRegisteredEntryInConfigRegistry(registeredEnvVariable, credentials.username(), EnvVariableConfigurationEntry.class);
    }

    public void assertRegisteredPasswordUnderProperty(@NonNull String registeredProperty) {
        var credentials = rabbitMQ.getCredentials();

        assertRegisteredEntryInConfigRegistry(registeredProperty, credentials.password(), PropertyConfigurationEntry.class);
    }

    public void assertRegisteredPasswordUnderEnvironmentVariable(@NonNull String registeredEnvVariable) {
        var credentials = rabbitMQ.getCredentials();

        assertRegisteredEntryInConfigRegistry(registeredEnvVariable, credentials.password(), EnvVariableConfigurationEntry.class);
    }

    public void assertNoEntriesRegistered() {
        var entriesRegistered = rabbitMQ
                .getConfigurationRegistry()
                .getEntries();

        assertThat(entriesRegistered).isEmpty();
    }

    private void assertRabbitMQAccessibility(Endpoint endpoint) {
        var connectionFactory = createConnectionFactory(endpoint);

        try(var connection = connectionFactory.newConnection()) {
            assertThat(connection.isOpen()).isTrue();
        } catch (Exception ex) {
            throw new AssertionError("Expected RabbitMQ to be accessible", ex);
        }
    }

    private void assertRegisteredEntryInConfigRegistry(String entryName, String entryValue, Class<? extends ConfigurationEntry> configType) {
        var configRegistry = rabbitMQ.getConfigurationRegistry();

        configRegistry
                .getEntries()
                .stream()
                .filter(configEntry -> configEntry.getClass().equals(configType))
                .filter(configEntry -> configEntry.name().equals(entryName))
                .findFirst()
                .ifPresentOrElse(
                        configEntry -> assertThat(configEntry.value()).isEqualTo(entryValue),
                        () -> { throw new AssertionError("Expected registered entry in config registry. Found none"); }
                );
    }

    private Client createManagementClient(Endpoint endpoint) {
        var credentials = rabbitMQ.getCredentials();

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

    private ConnectionFactory createConnectionFactory(Endpoint endpoint) {
        var credentials = rabbitMQ.getCredentials();

        try {
            var connectionFactory = new ConnectionFactory();
            connectionFactory.setUri(endpoint.address());
            connectionFactory.setUsername(credentials.username());
            connectionFactory.setPassword(credentials.password());
            connectionFactory.setConnectionTimeout((int) CONNECTION_TIMEOUT.toMillis());

            return connectionFactory;
        } catch (Exception ex) {
            throw new IllegalStateException("Could not create RabbitMQ connection factory", ex);
        }

    }
}
