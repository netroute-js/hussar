package pl.netroute.hussar.service.rabbitmq.api;

import com.rabbitmq.client.ConnectionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import pl.netroute.hussar.core.api.Endpoint;
import pl.netroute.hussar.core.configuration.api.ConfigurationEntry;
import pl.netroute.hussar.core.configuration.api.DefaultConfigurationRegistry;
import pl.netroute.hussar.core.helper.SchemesHelper;
import pl.netroute.hussar.core.network.api.NetworkConfigurer;
import pl.netroute.hussar.core.service.ServiceStartupContext;
import pl.netroute.hussar.core.service.registerer.EndpointRegisterer;
import pl.netroute.hussar.core.stub.helper.GenericContainerStubHelper.GenericContainerAccessibility;
import pl.netroute.hussar.core.stub.helper.StubHelper;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static pl.netroute.hussar.core.assertion.helper.NetworkConfigurerAssertionHelper.assertNetworkConfigured;
import static pl.netroute.hussar.core.docker.DockerHostResolver.DOCKER_BRIDGE_HOST;
import static pl.netroute.hussar.core.docker.DockerHostResolver.DOCKER_HOST_GATEWAY;
import static pl.netroute.hussar.core.helper.SchemesHelper.EMPTY_SCHEME;
import static pl.netroute.hussar.core.helper.SchemesHelper.HTTP_SCHEME;
import static pl.netroute.hussar.core.service.assertion.GenericContainerAssertionHelper.assertContainerEnvVariablesConfigured;
import static pl.netroute.hussar.core.service.assertion.GenericContainerAssertionHelper.assertContainerExposedPortConfigured;
import static pl.netroute.hussar.core.service.assertion.GenericContainerAssertionHelper.assertContainerExtraHostConfigured;
import static pl.netroute.hussar.core.service.assertion.GenericContainerAssertionHelper.assertContainerLoggingConfigured;
import static pl.netroute.hussar.core.service.assertion.GenericContainerAssertionHelper.assertContainerStarted;
import static pl.netroute.hussar.core.service.assertion.GenericContainerAssertionHelper.assertContainerStopped;
import static pl.netroute.hussar.core.service.assertion.GenericContainerAssertionHelper.assertContainerWaitStrategyConfigured;
import static pl.netroute.hussar.core.service.assertion.ServiceAssertionHelper.assertEntriesRegistered;
import static pl.netroute.hussar.core.service.assertion.ServiceAssertionHelper.assertName;
import static pl.netroute.hussar.core.service.assertion.ServiceAssertionHelper.assertNetworkControl;
import static pl.netroute.hussar.core.service.assertion.ServiceAssertionHelper.assertNoEntriesRegistered;
import static pl.netroute.hussar.core.service.assertion.ServiceAssertionHelper.assertSingleEndpoint;
import static pl.netroute.hussar.core.stub.helper.GenericContainerStubHelper.givenContainerAccessible;
import static pl.netroute.hussar.core.stub.helper.NetworkConfigurerStubHelper.givenNetworkConfigured;

public class RabbitMQDockerServiceTest {
    private static final String RABBITMQ_HOST = "localhost";

    private static final int RABBITMQ_LISTENING_PORT = 5672;
    private static final int RABBITMQ_MAPPED_PORT = 25672;

    private static final int RABBITMQ_MANAGEMENT_API_LISTENING_PORT = 15672;
    private static final int RABBITMQ_MANAGEMENT_API_MAPPED_PORT = 35672;

    private static final String RABBITMQ_KAFKA_SERVICE_NAME = "rabbitmq-service";
    private static final String RABBITMQ_SERVICE_IMAGE = "rabbitmq";
    private static final String RABBITMQ_MANAGEMENT_API_SERVICE_IMAGE = "rabbitmq:3.12.14-management-alpine";

    private static final String RABBITMQ_DEFAULT_USER_ENV = "RABBITMQ_DEFAULT_USER";
    private static final String RABBITMQ_DEFAULT_PASS_ENV = "RABBITMQ_DEFAULT_PASS";

    private static final String RABBITMQ_USERNAME = "guest";
    private static final String RABBITMQ_PASSWORD = "password";

    private static final boolean DURABLE = false;
    private static final boolean EXCLUSIVE = false;
    private static final boolean AUTO_DELETE = false;

    private NetworkConfigurer networkConfigurer;
    private RabbitMQQueueConfigurer queueConfigurer;

    private GenericContainerAccessibility containerAccessibility;

    @BeforeEach
    public void setup() {
        networkConfigurer = StubHelper.defaultStub(NetworkConfigurer.class);
        queueConfigurer = StubHelper.defaultStub(RabbitMQQueueConfigurer.class);

        containerAccessibility = GenericContainerAccessibility
                .builder()
                .host(RABBITMQ_HOST)
                .exposedPort(RABBITMQ_LISTENING_PORT)
                .mappedPort(RABBITMQ_LISTENING_PORT, RABBITMQ_MAPPED_PORT)
                .build();
    }

    @Test
    public void shouldStartMinimalService() {
        // given
        var config = RabbitMQDockerServiceConfig
                .builder()
                .name(RABBITMQ_KAFKA_SERVICE_NAME)
                .dockerImage(RABBITMQ_SERVICE_IMAGE)
                .scheme(EMPTY_SCHEME)
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

        var endpoint = Endpoint.of(EMPTY_SCHEME, RABBITMQ_HOST, RABBITMQ_MAPPED_PORT);

        var container = StubHelper.defaultStub(GenericContainer.class);
        var service = createRabbitMQService(config, container);

        givenContainerAccessible(container, containerAccessibility);
        givenNetworkConfigured(networkConfigurer, RABBITMQ_KAFKA_SERVICE_NAME, endpoint);

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
        assertContainerExtraHostConfigured(container, DOCKER_BRIDGE_HOST, DOCKER_HOST_GATEWAY);
        assertContainerLoggingConfigured(container);
        assertContainerEnvVariablesConfigured(container, envVariables);
        assertName(service, RABBITMQ_KAFKA_SERVICE_NAME);
        assertSingleEndpoint(service, endpoint);
        assertNetworkControl(service);
        assertNoQueuesCreated(queueConfigurer);
        assertNoEntriesRegistered(service);
        assertNetworkConfigured(networkConfigurer, RABBITMQ_KAFKA_SERVICE_NAME, endpoint);
    }

    @Test
    public void shouldStartMinimalServiceWithManagementApi() {
        // given
        var config = RabbitMQDockerServiceConfig
                .builder()
                .name(RABBITMQ_KAFKA_SERVICE_NAME)
                .dockerImage(RABBITMQ_MANAGEMENT_API_SERVICE_IMAGE)
                .scheme(EMPTY_SCHEME)
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

        containerAccessibility = containerAccessibility
                .toBuilder()
                .exposedPort(RABBITMQ_MANAGEMENT_API_LISTENING_PORT)
                .mappedPort(RABBITMQ_MANAGEMENT_API_LISTENING_PORT, RABBITMQ_MANAGEMENT_API_MAPPED_PORT)
                .build();

        var endpoint = Endpoint.of(EMPTY_SCHEME, RABBITMQ_HOST, RABBITMQ_MAPPED_PORT);

        var container = StubHelper.defaultStub(GenericContainer.class);
        var service = createRabbitMQService(config, container);

        givenContainerAccessible(container, containerAccessibility);
        givenNetworkConfigured(networkConfigurer, RABBITMQ_KAFKA_SERVICE_NAME, endpoint);

        // when
        service.start(ServiceStartupContext.defaultContext());

        // then
        var envVariables = Map.of(
                RABBITMQ_DEFAULT_USER_ENV, RABBITMQ_USERNAME,
                RABBITMQ_DEFAULT_PASS_ENV, RABBITMQ_PASSWORD
        );

        assertContainerStarted(container);
        assertContainerExposedPortConfigured(container, RABBITMQ_LISTENING_PORT, RABBITMQ_MANAGEMENT_API_LISTENING_PORT);
        assertContainerWaitStrategyConfigured(container, Wait.forListeningPort());
        assertContainerExtraHostConfigured(container, DOCKER_BRIDGE_HOST, DOCKER_HOST_GATEWAY);
        assertContainerLoggingConfigured(container);
        assertContainerEnvVariablesConfigured(container, envVariables);
        assertName(service, RABBITMQ_KAFKA_SERVICE_NAME);
        assertSingleEndpoint(service, endpoint);
        assertNetworkControl(service);
        assertNoQueuesCreated(queueConfigurer);
        assertNoEntriesRegistered(service);
        assertNetworkConfigured(networkConfigurer, RABBITMQ_KAFKA_SERVICE_NAME, endpoint);
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
                .name(RABBITMQ_KAFKA_SERVICE_NAME)
                .dockerImage(RABBITMQ_MANAGEMENT_API_SERVICE_IMAGE)
                .scheme(EMPTY_SCHEME)
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

        containerAccessibility = containerAccessibility
                .toBuilder()
                .exposedPort(RABBITMQ_MANAGEMENT_API_LISTENING_PORT)
                .mappedPort(RABBITMQ_MANAGEMENT_API_LISTENING_PORT, RABBITMQ_MANAGEMENT_API_MAPPED_PORT)
                .build();

        var endpoint = Endpoint.of(EMPTY_SCHEME, RABBITMQ_HOST, RABBITMQ_MAPPED_PORT);

        var container = StubHelper.defaultStub(GenericContainer.class);
        var service = createRabbitMQService(config, container);

        givenContainerAccessible(container, containerAccessibility);
        givenNetworkConfigured(networkConfigurer, RABBITMQ_KAFKA_SERVICE_NAME, endpoint);

        // when
        service.start(ServiceStartupContext.defaultContext());

        // then
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
        assertContainerExtraHostConfigured(container, DOCKER_BRIDGE_HOST, DOCKER_HOST_GATEWAY);
        assertContainerLoggingConfigured(container);
        assertContainerEnvVariablesConfigured(container, envVariables);
        assertName(service, RABBITMQ_KAFKA_SERVICE_NAME);
        assertSingleEndpoint(service, endpoint);
        assertNetworkControl(service);
        assertQueuesCreated(queueConfigurer, queues);
        assertEntriesRegistered(service, registeredEntries);
        assertNetworkConfigured(networkConfigurer, RABBITMQ_KAFKA_SERVICE_NAME, endpoint);
    }

    @Test
    public void shouldShutdownService() {
        // given
        var config = RabbitMQDockerServiceConfig
                .builder()
                .name(RABBITMQ_KAFKA_SERVICE_NAME)
                .dockerImage(RABBITMQ_SERVICE_IMAGE)
                .scheme(EMPTY_SCHEME)
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

        givenContainerAccessible(container, containerAccessibility);

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
                .name(RABBITMQ_KAFKA_SERVICE_NAME)
                .dockerImage(RABBITMQ_SERVICE_IMAGE)
                .scheme(EMPTY_SCHEME)
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

    @Test
    public void shouldReturnManagementApiEndpoint() {
        // given
        var config = RabbitMQDockerServiceConfig
                .builder()
                .name(RABBITMQ_KAFKA_SERVICE_NAME)
                .dockerImage(RABBITMQ_MANAGEMENT_API_SERVICE_IMAGE)
                .scheme(EMPTY_SCHEME)
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

        containerAccessibility = containerAccessibility
                .toBuilder()
                .exposedPort(RABBITMQ_MANAGEMENT_API_LISTENING_PORT)
                .mappedPort(RABBITMQ_MANAGEMENT_API_LISTENING_PORT, RABBITMQ_MANAGEMENT_API_MAPPED_PORT)
                .build();

        var endpoint = Endpoint.of(EMPTY_SCHEME, RABBITMQ_HOST, RABBITMQ_MAPPED_PORT);

        var container = StubHelper.defaultStub(GenericContainer.class);
        var service = createRabbitMQService(config, container);

        givenContainerAccessible(container, containerAccessibility);
        givenNetworkConfigured(networkConfigurer, RABBITMQ_KAFKA_SERVICE_NAME, endpoint);

        // when
        service.start(ServiceStartupContext.defaultContext());

        var managementEndpoint = service.getManagementEndpoint();

        // then
        assertManagementApiEndpoint(managementEndpoint);
    }

    @Test
    public void shouldReturnNoManagementApiEndpoint() {
        // given
        var config = RabbitMQDockerServiceConfig
                .builder()
                .name(RABBITMQ_KAFKA_SERVICE_NAME)
                .dockerImage(RABBITMQ_SERVICE_IMAGE)
                .scheme(EMPTY_SCHEME)
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

        var endpoint = Endpoint.of(EMPTY_SCHEME, RABBITMQ_HOST, RABBITMQ_MAPPED_PORT);

        var container = StubHelper.defaultStub(GenericContainer.class);
        var service = createRabbitMQService(config, container);

        givenContainerAccessible(container, containerAccessibility);
        givenNetworkConfigured(networkConfigurer, RABBITMQ_KAFKA_SERVICE_NAME, endpoint);

        // when
        var managementEndpoint = service.getManagementEndpoint();

        // then
        assertNoManagementApiEndpoint(managementEndpoint);
    }

    private RabbitMQDockerService createRabbitMQService(RabbitMQDockerServiceConfig config,
                                                        GenericContainer<?> container) {
        var configurationRegistry = new DefaultConfigurationRegistry();
        var endpointRegisterer = new EndpointRegisterer(configurationRegistry);
        var credentialsRegisterer = new RabbitMQCredentialsRegisterer(configurationRegistry);

        return new RabbitMQDockerService(
                container,
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

    private void assertManagementApiEndpoint(Optional<Endpoint> maybeManagementEndpoint) {
        var expectedManagementEndpoint = Endpoint.of(SchemesHelper.HTTP_SCHEME, RABBITMQ_HOST, RABBITMQ_MANAGEMENT_API_MAPPED_PORT);

        assertThat(maybeManagementEndpoint).hasValue(expectedManagementEndpoint);
    }

    private void assertNoManagementApiEndpoint(Optional<Endpoint> maybeManagementEndpoint) {
        assertThat(maybeManagementEndpoint).isEmpty();
    }

}
