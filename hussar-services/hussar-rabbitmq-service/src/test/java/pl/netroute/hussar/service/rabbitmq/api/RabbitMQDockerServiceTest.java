package pl.netroute.hussar.service.rabbitmq.api;

import com.rabbitmq.client.ConnectionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import pl.netroute.hussar.core.api.Endpoint;
import pl.netroute.hussar.core.configuration.api.ConfigurationEntry;
import pl.netroute.hussar.core.configuration.api.DefaultConfigurationRegistry;
import pl.netroute.hussar.core.docker.api.DockerNetwork;
import pl.netroute.hussar.core.network.api.NetworkConfigurer;
import pl.netroute.hussar.core.service.ServiceStartupContext;
import pl.netroute.hussar.core.service.registerer.EndpointRegisterer;
import pl.netroute.hussar.core.stub.helper.GenericContainerStubHelper.GenericContainerAccessibility;
import pl.netroute.hussar.core.stub.helper.StubHelper;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static pl.netroute.hussar.core.helper.SchemesHelper.HTTP_SCHEME;
import static pl.netroute.hussar.core.service.assertion.GenericContainerAssertionHelper.assertContainerEnvVariablesConfigured;
import static pl.netroute.hussar.core.service.assertion.GenericContainerAssertionHelper.assertContainerExposedPortConfigured;
import static pl.netroute.hussar.core.service.assertion.GenericContainerAssertionHelper.assertContainerLoggingConfigured;
import static pl.netroute.hussar.core.service.assertion.GenericContainerAssertionHelper.assertContainerNetworkConfigured;
import static pl.netroute.hussar.core.service.assertion.GenericContainerAssertionHelper.assertContainerStarted;
import static pl.netroute.hussar.core.service.assertion.GenericContainerAssertionHelper.assertContainerStartupTimeoutConfigured;
import static pl.netroute.hussar.core.service.assertion.GenericContainerAssertionHelper.assertContainerStopped;
import static pl.netroute.hussar.core.service.assertion.GenericContainerAssertionHelper.assertContainerWaitStrategyConfigured;
import static pl.netroute.hussar.core.service.assertion.ServiceAssertionHelper.assertDirectEndpoints;
import static pl.netroute.hussar.core.service.assertion.ServiceAssertionHelper.assertEndpoints;
import static pl.netroute.hussar.core.service.assertion.ServiceAssertionHelper.assertEntriesRegistered;
import static pl.netroute.hussar.core.service.assertion.ServiceAssertionHelper.assertName;
import static pl.netroute.hussar.core.service.assertion.ServiceAssertionHelper.assertNetworkControl;
import static pl.netroute.hussar.core.service.assertion.ServiceAssertionHelper.assertNoEntriesRegistered;
import static pl.netroute.hussar.core.stub.helper.GenericContainerStubHelper.givenContainerAccessible;
import static pl.netroute.hussar.core.stub.helper.NetworkConfigurerStubHelper.givenNetworkConfigured;

public class RabbitMQDockerServiceTest {
    private static final String RABBITMQ_HOST = "localhost";

    private static final int RABBITMQ_LISTENING_PORT = 5672;

    private static final Duration RABBITMQ_STARTUP_TIMEOUT = Duration.ofSeconds(90L);

    private static final int RABBITMQ_MANAGEMENT_API_LISTENING_PORT = 15672;
    private static final int RABBITMQ_MANAGEMENT_API_MAPPED_PORT = 35672;

    private static final String RABBITMQ_SERVICE_NAME = "rabbitmq-service";
    private static final String RABBITMQ_SERVICE_IMAGE = "rabbitmq";
    private static final String RABBITMQ_MANAGEMENT_API_SERVICE_IMAGE = "rabbitmq:3.12.14-management-alpine";

    private static final String RABBITMQ_DIRECT_NETWORK = "direct-" + RABBITMQ_SERVICE_NAME;

    private static final String RABBITMQ_SCHEME = "amqp";

    private static final String RABBITMQ_DEFAULT_USER_ENV = "RABBITMQ_DEFAULT_USER";
    private static final String RABBITMQ_DEFAULT_PASS_ENV = "RABBITMQ_DEFAULT_PASS";

    private static final String RABBITMQ_USERNAME = "guest";
    private static final String RABBITMQ_PASSWORD = "password";

    private static final boolean DURABLE = false;
    private static final boolean EXCLUSIVE = false;
    private static final boolean AUTO_DELETE = false;

    private DockerNetwork dockerNetwork;
    private NetworkConfigurer networkConfigurer;
    private RabbitMQQueueConfigurer queueConfigurer;

    @BeforeEach
    public void setup() {
        dockerNetwork = StubHelper.defaultStub(DockerNetwork.class);
        networkConfigurer = StubHelper.defaultStub(NetworkConfigurer.class);
        queueConfigurer = StubHelper.defaultStub(RabbitMQQueueConfigurer.class);
    }

    @Test
    public void shouldStartMinimalService() {
        // given
        var config = RabbitMQDockerServiceConfig
                .builder()
                .name(RABBITMQ_SERVICE_NAME)
                .dockerImage(RABBITMQ_SERVICE_IMAGE)
                .startupTimeout(RABBITMQ_STARTUP_TIMEOUT)
                .scheme(RABBITMQ_SCHEME)
                .queues(Set.of())
                .registerEndpointUnderProperties(Set.of())
                .registerEndpointUnderEnvironmentVariables(Set.of())
                .registerUsernameUnderProperties(Set.of())
                .registerUsernameUnderEnvironmentVariables(Set.of())
                .registerPasswordUnderProperties(Set.of())
                .registerPasswordUnderEnvironmentVariables(Set.of())
                .registerManagementEndpointUnderProperties(Set.of())
                .registerManagementEndpointUnderEnvironmentVariables(Set.of())
                .build();

        var container = StubHelper.defaultStub(GenericContainer.class);
        var service = createRabbitMQService(config, container);

        var network = givenNetworkConfigured(networkConfigurer, RABBITMQ_SERVICE_NAME, RABBITMQ_SCHEME, RABBITMQ_LISTENING_PORT);
        var directNetwork = givenNetworkConfigured(networkConfigurer, RABBITMQ_DIRECT_NETWORK, RABBITMQ_SCHEME, RABBITMQ_LISTENING_PORT);

        // when
        service.start(ServiceStartupContext.defaultContext());

        // then
        var envVariables = Map.of(
                RABBITMQ_DEFAULT_USER_ENV, RABBITMQ_USERNAME,
                RABBITMQ_DEFAULT_PASS_ENV, RABBITMQ_PASSWORD
        );

        assertContainerStarted(container);
        assertContainerExposedPortConfigured(container, RABBITMQ_LISTENING_PORT);
        assertContainerWaitStrategyConfigured(container, Wait.forListeningPort());
        assertContainerStartupTimeoutConfigured(container, RABBITMQ_STARTUP_TIMEOUT);
        assertContainerNetworkConfigured(container, dockerNetwork);
        assertContainerLoggingConfigured(container);
        assertContainerEnvVariablesConfigured(container, envVariables);
        assertName(service, RABBITMQ_SERVICE_NAME);
        assertEndpoints(service, network);
        assertDirectEndpoints(service, directNetwork);
        assertNetworkControl(service);
        assertNoQueuesCreated(queueConfigurer);
        assertNoEntriesRegistered(service);
        assertNoManagementApiEndpoint(service);
    }

    @Test
    public void shouldStartMinimalServiceWithManagementApi() {
        // given
        var config = RabbitMQDockerServiceConfig
                .builder()
                .name(RABBITMQ_SERVICE_NAME)
                .dockerImage(RABBITMQ_MANAGEMENT_API_SERVICE_IMAGE)
                .startupTimeout(RABBITMQ_STARTUP_TIMEOUT)
                .scheme(RABBITMQ_SCHEME)
                .queues(Set.of())
                .registerEndpointUnderProperties(Set.of())
                .registerEndpointUnderEnvironmentVariables(Set.of())
                .registerUsernameUnderProperties(Set.of())
                .registerUsernameUnderEnvironmentVariables(Set.of())
                .registerPasswordUnderProperties(Set.of())
                .registerPasswordUnderEnvironmentVariables(Set.of())
                .registerManagementEndpointUnderProperties(Set.of())
                .registerManagementEndpointUnderEnvironmentVariables(Set.of())
                .build();

        var containerAccessibility = GenericContainerAccessibility
                .builder()
                .host(RABBITMQ_HOST)
                .exposedPort(RABBITMQ_MANAGEMENT_API_LISTENING_PORT)
                .mappedPort(RABBITMQ_MANAGEMENT_API_LISTENING_PORT, RABBITMQ_MANAGEMENT_API_MAPPED_PORT)
                .build();

        var container = StubHelper.defaultStub(GenericContainer.class);
        var service = createRabbitMQService(config, container);

        givenContainerAccessible(container, containerAccessibility);
        var network = givenNetworkConfigured(networkConfigurer, RABBITMQ_SERVICE_NAME, RABBITMQ_SCHEME, RABBITMQ_LISTENING_PORT);
        var directNetwork = givenNetworkConfigured(networkConfigurer, RABBITMQ_DIRECT_NETWORK, RABBITMQ_SCHEME, RABBITMQ_LISTENING_PORT);

        // when
        service.start(ServiceStartupContext.defaultContext());

        // then
        var managementEndpoint = Endpoint.of(HTTP_SCHEME, RABBITMQ_HOST, RABBITMQ_MANAGEMENT_API_MAPPED_PORT);

        var envVariables = Map.of(
                RABBITMQ_DEFAULT_USER_ENV, RABBITMQ_USERNAME,
                RABBITMQ_DEFAULT_PASS_ENV, RABBITMQ_PASSWORD
        );

        assertContainerStarted(container);
        assertContainerExposedPortConfigured(container, RABBITMQ_LISTENING_PORT, RABBITMQ_MANAGEMENT_API_LISTENING_PORT);
        assertContainerWaitStrategyConfigured(container, Wait.forListeningPort());
        assertContainerStartupTimeoutConfigured(container, RABBITMQ_STARTUP_TIMEOUT);
        assertContainerNetworkConfigured(container, dockerNetwork);
        assertContainerLoggingConfigured(container);
        assertContainerEnvVariablesConfigured(container, envVariables);
        assertName(service, RABBITMQ_SERVICE_NAME);
        assertEndpoints(service, network);
        assertDirectEndpoints(service, directNetwork);
        assertNetworkControl(service);
        assertNoQueuesCreated(queueConfigurer);
        assertNoEntriesRegistered(service);
        assertManagementApiEndpoint(service, managementEndpoint);
    }

    @Test
    public void shouldStartExtendedService() {
        // given
        var endpointProperty = "endpoint.url";
        var endpointEnvVariable = "ENDPOINT_URL";

        var managementEndpointProperty = "management.endpoint.url";
        var managementEndpointEnvVariable = "MANAGEMENT_ENDPOINT_URL";

        var usernameProperty = "redis.username";
        var usernameEnvVariable = "REDIS_USERNAME";

        var passwordProperty = "redis.password";
        var passwordEnvVariable = "REDIS_PASSWORD";

        var queueNameA = "queueA";
        var queueNameB = "queueB";

        var queueArgumentsB = Map.<String, Object>of(
                "x-message-ttl", 60000L
        );

        var queueA = new RabbitMQQueue(queueNameA, DURABLE, EXCLUSIVE, AUTO_DELETE, Map.of());
        var queueB = new RabbitMQQueue(queueNameB, DURABLE, EXCLUSIVE, AUTO_DELETE, queueArgumentsB);
        var queues = Set.of(queueA, queueB);

        var config = RabbitMQDockerServiceConfig
                .builder()
                .name(RABBITMQ_SERVICE_NAME)
                .dockerImage(RABBITMQ_MANAGEMENT_API_SERVICE_IMAGE)
                .startupTimeout(RABBITMQ_STARTUP_TIMEOUT)
                .scheme(RABBITMQ_SCHEME)
                .queues(queues)
                .registerEndpointUnderProperties(Set.of(endpointProperty))
                .registerEndpointUnderEnvironmentVariables(Set.of(endpointEnvVariable))
                .registerUsernameUnderProperties(Set.of(usernameProperty))
                .registerUsernameUnderEnvironmentVariables(Set.of(usernameEnvVariable))
                .registerPasswordUnderProperties(Set.of(passwordProperty))
                .registerPasswordUnderEnvironmentVariables(Set.of(passwordEnvVariable))
                .registerManagementEndpointUnderProperties(Set.of(managementEndpointProperty))
                .registerManagementEndpointUnderEnvironmentVariables(Set.of(managementEndpointEnvVariable))
                .build();

        var containerAccessibility = GenericContainerAccessibility
                .builder()
                .host(RABBITMQ_HOST)
                .exposedPort(RABBITMQ_MANAGEMENT_API_LISTENING_PORT)
                .mappedPort(RABBITMQ_MANAGEMENT_API_LISTENING_PORT, RABBITMQ_MANAGEMENT_API_MAPPED_PORT)
                .build();

        var container = StubHelper.defaultStub(GenericContainer.class);
        var service = createRabbitMQService(config, container);

        givenContainerAccessible(container, containerAccessibility);
        var network = givenNetworkConfigured(networkConfigurer, RABBITMQ_SERVICE_NAME, RABBITMQ_SCHEME, RABBITMQ_LISTENING_PORT);
        var directNetwork = givenNetworkConfigured(networkConfigurer, RABBITMQ_DIRECT_NETWORK, RABBITMQ_SCHEME, RABBITMQ_LISTENING_PORT);

        // when
        service.start(ServiceStartupContext.defaultContext());

        // then
        var endpoint = network.getEndpoints().getFirst();
        var endpointPropertyEntry = ConfigurationEntry.property(endpointProperty, endpoint.address());
        var endpointEnvVariableEntry = ConfigurationEntry.envVariable(endpointEnvVariable, endpoint.address());

        var managementEndpoint = Endpoint.of(HTTP_SCHEME, RABBITMQ_HOST, RABBITMQ_MANAGEMENT_API_MAPPED_PORT);
        var managementEndpointPropertyEntry = ConfigurationEntry.property(managementEndpointProperty, managementEndpoint.address());
        var managementEndpointEnvVariableEntry = ConfigurationEntry.envVariable(managementEndpointEnvVariable, managementEndpoint.address());

        var usernamePropertyEntry = ConfigurationEntry.property(usernameProperty, RABBITMQ_USERNAME);
        var usernameEnvVariableEntry = ConfigurationEntry.envVariable(usernameEnvVariable, RABBITMQ_USERNAME);

        var passwordPropertyEntry = ConfigurationEntry.property(passwordProperty, RABBITMQ_PASSWORD);
        var passwordEnvVariableEntry = ConfigurationEntry.envVariable(passwordEnvVariable, RABBITMQ_PASSWORD);

        var registeredEntries = List.<ConfigurationEntry>of(
                endpointPropertyEntry,
                endpointEnvVariableEntry,
                managementEndpointPropertyEntry,
                managementEndpointEnvVariableEntry,
                usernamePropertyEntry,
                usernameEnvVariableEntry,
                passwordPropertyEntry,
                passwordEnvVariableEntry
        );

        var envVariables = Map.of(
                RABBITMQ_DEFAULT_USER_ENV, RABBITMQ_USERNAME,
                RABBITMQ_DEFAULT_PASS_ENV, RABBITMQ_PASSWORD
        );

        assertContainerStarted(container);
        assertContainerExposedPortConfigured(container, RABBITMQ_LISTENING_PORT, RABBITMQ_MANAGEMENT_API_LISTENING_PORT);
        assertContainerWaitStrategyConfigured(container, Wait.forListeningPort());
        assertContainerStartupTimeoutConfigured(container, RABBITMQ_STARTUP_TIMEOUT);
        assertContainerNetworkConfigured(container, dockerNetwork);
        assertContainerLoggingConfigured(container);
        assertContainerEnvVariablesConfigured(container, envVariables);
        assertName(service, RABBITMQ_SERVICE_NAME);
        assertEndpoints(service, network);
        assertDirectEndpoints(service, directNetwork);
        assertNetworkControl(service);
        assertQueuesCreated(queueConfigurer, queues);
        assertEntriesRegistered(service, registeredEntries);
        assertManagementApiEndpoint(service, managementEndpoint);
    }

    @Test
    public void shouldShutdownService() {
        // given
        var config = RabbitMQDockerServiceConfig
                .builder()
                .name(RABBITMQ_SERVICE_NAME)
                .dockerImage(RABBITMQ_SERVICE_IMAGE)
                .startupTimeout(RABBITMQ_STARTUP_TIMEOUT)
                .scheme(RABBITMQ_SCHEME)
                .queues(Set.of())
                .registerEndpointUnderProperties(Set.of())
                .registerEndpointUnderEnvironmentVariables(Set.of())
                .registerUsernameUnderProperties(Set.of())
                .registerUsernameUnderEnvironmentVariables(Set.of())
                .registerPasswordUnderProperties(Set.of())
                .registerPasswordUnderEnvironmentVariables(Set.of())
                .registerManagementEndpointUnderProperties(Set.of())
                .registerManagementEndpointUnderEnvironmentVariables(Set.of())
                .build();

        var container = StubHelper.defaultStub(GenericContainer.class);
        var service = createRabbitMQService(config, container);

        // when
        service.shutdown();

        // then
        assertContainerStopped(container);
    }

    @Test
    public void shouldGetCredentials() {
        // given
        var config = RabbitMQDockerServiceConfig
                .builder()
                .name(RABBITMQ_SERVICE_NAME)
                .dockerImage(RABBITMQ_SERVICE_IMAGE)
                .startupTimeout(RABBITMQ_STARTUP_TIMEOUT)
                .scheme(RABBITMQ_SCHEME)
                .queues(Set.of())
                .registerEndpointUnderProperties(Set.of())
                .registerEndpointUnderEnvironmentVariables(Set.of())
                .registerUsernameUnderProperties(Set.of())
                .registerUsernameUnderEnvironmentVariables(Set.of())
                .registerPasswordUnderProperties(Set.of())
                .registerPasswordUnderEnvironmentVariables(Set.of())
                .registerManagementEndpointUnderProperties(Set.of())
                .registerManagementEndpointUnderEnvironmentVariables(Set.of())
                .build();

        var container = StubHelper.defaultStub(GenericContainer.class);
        var service = createRabbitMQService(config, container);

        // when
        var credentials = service.getCredentials();

        // then
        assertCredentials(credentials);
    }

    private RabbitMQDockerService createRabbitMQService(RabbitMQDockerServiceConfig config,
                                                        GenericContainer<?> container) {
        var configurationRegistry = new DefaultConfigurationRegistry();
        var endpointRegisterer = new EndpointRegisterer(configurationRegistry);
        var credentialsRegisterer = new RabbitMQCredentialsRegisterer(configurationRegistry);

        return new RabbitMQDockerService(
                container,
                dockerNetwork,
                config,
                configurationRegistry,
                endpointRegisterer,
                networkConfigurer,
                credentialsRegisterer,
                queueConfigurer
        );
    }

    private void assertQueuesCreated(RabbitMQQueueConfigurer queueConfigurer,
                                     Set<RabbitMQQueue> queues) {
        queues.forEach(queue -> verify(queueConfigurer).configure(any(ConnectionFactory.class), eq(queue)));
    }

    private void assertNoQueuesCreated(RabbitMQQueueConfigurer queueConfigurer) {
        verify(queueConfigurer, never()).configure(any(), any());
    }

    private void assertCredentials(RabbitMQCredentials credentials) {
        assertThat(credentials.username()).isEqualTo(RABBITMQ_USERNAME);
        assertThat(credentials.password()).isEqualTo(RABBITMQ_PASSWORD);
    }

    private void assertManagementApiEndpoint(RabbitMQDockerService rabbitService, Endpoint expectedManagementEndpoint) {
        var maybeManagementEndpoint = rabbitService.getManagementEndpoint();

        assertThat(maybeManagementEndpoint).hasValue(expectedManagementEndpoint);
    }

    private void assertNoManagementApiEndpoint(RabbitMQDockerService rabbitService) {
        var maybeManagementEndpoint = rabbitService.getManagementEndpoint();

        assertThat(maybeManagementEndpoint).isEmpty();
    }

}
