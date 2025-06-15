package pl.netroute.hussar.service.kafka.api;

import org.apache.kafka.clients.admin.AdminClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.kafka.ConfluentKafkaContainer;
import pl.netroute.hussar.core.configuration.api.ConfigurationEntry;
import pl.netroute.hussar.core.configuration.api.DefaultConfigurationRegistry;
import pl.netroute.hussar.core.docker.api.DockerNetwork;
import pl.netroute.hussar.core.network.api.NetworkConfigurer;
import pl.netroute.hussar.core.service.ServiceStartupContext;
import pl.netroute.hussar.core.service.registerer.EndpointRegisterer;
import pl.netroute.hussar.core.stub.helper.StubHelper;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static pl.netroute.hussar.core.helper.SchemesHelper.EMPTY_SCHEME;
import static pl.netroute.hussar.core.service.assertion.GenericContainerAssertionHelper.assertContainerEnvVariablesConfigured;
import static pl.netroute.hussar.core.service.assertion.GenericContainerAssertionHelper.assertContainerExposedPortConfigured;
import static pl.netroute.hussar.core.service.assertion.GenericContainerAssertionHelper.assertContainerLoggingConfigured;
import static pl.netroute.hussar.core.service.assertion.GenericContainerAssertionHelper.assertContainerNetworkConfigured;
import static pl.netroute.hussar.core.service.assertion.GenericContainerAssertionHelper.assertContainerStarted;
import static pl.netroute.hussar.core.service.assertion.GenericContainerAssertionHelper.assertContainerStartupTimeoutConfigured;
import static pl.netroute.hussar.core.service.assertion.GenericContainerAssertionHelper.assertContainerStopped;
import static pl.netroute.hussar.core.service.assertion.GenericContainerAssertionHelper.assertContainerWaitStrategyConfigured;
import static pl.netroute.hussar.core.service.assertion.ServiceAssertionHelper.assertEndpoints;
import static pl.netroute.hussar.core.service.assertion.ServiceAssertionHelper.assertName;
import static pl.netroute.hussar.core.service.assertion.ServiceAssertionHelper.assertNetworkControl;
import static pl.netroute.hussar.core.service.assertion.ServiceAssertionHelper.assertNoEntriesRegistered;
import static pl.netroute.hussar.core.stub.helper.NetworkConfigurerStubHelper.givenNetworkConfigured;

public class KafkaDockerServiceTest {
    private static final int KAFKA_LISTENING_PORT = 9092;

    private static final Duration KAFKA_STARTUP_TIMEOUT = Duration.ofSeconds(90);

    private static final String KAFKA_SERVICE_NAME = "kafka-service";
    private static final String KAFKA_SERVICE_IMAGE = "confluentinc/cp-kafka";

    private static final String KAFKA_AUTO_CREATE_TOPICS_ENABLE_ENV = "KAFKA_AUTO_CREATE_TOPICS_ENABLE";
    private static final boolean KAFKA_AUTO_CREATE_TOPICS_DISABLED = false;
    private static final boolean KAFKA_AUTO_CREATE_TOPICS_ENABLED = true;

    private DockerNetwork dockerNetwork;
    private NetworkConfigurer networkConfigurer;
    private KafkaTopicConfigurer topicConfigurer;

    @BeforeEach
    public void setup() {
        dockerNetwork = StubHelper.defaultStub(DockerNetwork.class);
        networkConfigurer = StubHelper.defaultStub(NetworkConfigurer.class);
        topicConfigurer = StubHelper.defaultStub(KafkaTopicConfigurer.class);
    }

    @Test
    public void shouldStartMinimalService() {
        // given
        var config = KafkaDockerServiceConfig
                .builder()
                .name(KAFKA_SERVICE_NAME)
                .dockerImage(KAFKA_SERVICE_IMAGE)
                .startupTimeout(KAFKA_STARTUP_TIMEOUT)
                .scheme(EMPTY_SCHEME)
                .topics(Set.of())
                .registerEndpointUnderProperties(Set.of())
                .registerEndpointUnderEnvironmentVariables(Set.of())
                .build();

        var container = StubHelper.defaultStub(ConfluentKafkaContainer.class);
        var service = createKafkaService(config, container);

        var network = givenNetworkConfigured(networkConfigurer, KAFKA_SERVICE_NAME, EMPTY_SCHEME, KAFKA_LISTENING_PORT);

        // when
        service.start(ServiceStartupContext.defaultContext());

        // then
        var envVariables = Map.of(KAFKA_AUTO_CREATE_TOPICS_ENABLE_ENV, KAFKA_AUTO_CREATE_TOPICS_DISABLED + "");

        assertContainerStarted(container);
        assertContainerExposedPortConfigured(container, KAFKA_LISTENING_PORT);
        assertContainerWaitStrategyConfigured(container, Wait.forListeningPort());
        assertContainerStartupTimeoutConfigured(container, KAFKA_STARTUP_TIMEOUT);
        assertContainerNetworkConfigured(container, dockerNetwork);
        assertContainerLoggingConfigured(container);
        assertContainerEnvVariablesConfigured(container, envVariables);
        assertName(service, KAFKA_SERVICE_NAME);
        assertEndpoints(service, network);
        assertNetworkControl(service);
        assertNoTopicsCreated(topicConfigurer);
        assertNoEntriesRegistered(service);
    }

    @Test
    public void shouldStartExtendedService() {
        // given
        var endpointProperty = "endpoint.url";
        var endpointEnvVariable = "ENDPOINT_URL";

        var partitions = 5;
        var topicA = new KafkaTopic("topicA", partitions);
        var topicB = new KafkaTopic("topicB", partitions);
        var topics = Set.of(topicA, topicB);

        var config = KafkaDockerServiceConfig
                .builder()
                .name(KAFKA_SERVICE_NAME)
                .dockerImage(KAFKA_SERVICE_IMAGE)
                .startupTimeout(KAFKA_STARTUP_TIMEOUT)
                .scheme(EMPTY_SCHEME)
                .topics(topics)
                .topicAutoCreation(true)
                .registerEndpointUnderProperties(Set.of(endpointProperty))
                .registerEndpointUnderEnvironmentVariables(Set.of(endpointEnvVariable))
                .build();

        var container = StubHelper.defaultStub(ConfluentKafkaContainer.class);
        var service = createKafkaService(config, container);

        var network = givenNetworkConfigured(networkConfigurer, KAFKA_SERVICE_NAME, EMPTY_SCHEME, KAFKA_LISTENING_PORT);

        // when
        service.start(ServiceStartupContext.defaultContext());

        // then
        var endpoint = network.getEndpoints().getFirst();
        var endpointPropertyEntry = ConfigurationEntry.property(endpointProperty, endpoint.address());
        var endpointEnvVariableEntry = ConfigurationEntry.envVariable(endpointEnvVariable, endpoint.address());

        var registeredEntries = List.<ConfigurationEntry>of(
                endpointPropertyEntry,
                endpointEnvVariableEntry
        );

        var envVariables = Map.of(KAFKA_AUTO_CREATE_TOPICS_ENABLE_ENV, KAFKA_AUTO_CREATE_TOPICS_ENABLED + "");

        assertContainerStarted(container);
        assertContainerExposedPortConfigured(container, KAFKA_LISTENING_PORT);
        assertContainerWaitStrategyConfigured(container, Wait.forListeningPort());
        assertContainerStartupTimeoutConfigured(container, KAFKA_STARTUP_TIMEOUT);
        assertContainerNetworkConfigured(container, dockerNetwork);
        assertContainerLoggingConfigured(container);
        assertContainerEnvVariablesConfigured(container, envVariables);
        assertName(service, KAFKA_SERVICE_NAME);
        assertEndpoints(service, network);
        assertNetworkControl(service);
        assertTopicsCreated(topicConfigurer, topics);
    }

    @Test
    public void shouldShutdownService() {
        // given
        var config = KafkaDockerServiceConfig
                .builder()
                .name(KAFKA_SERVICE_NAME)
                .dockerImage(KAFKA_SERVICE_IMAGE)
                .startupTimeout(KAFKA_STARTUP_TIMEOUT)
                .scheme(EMPTY_SCHEME)
                .topics(Set.of())
                .registerEndpointUnderProperties(Set.of())
                .registerEndpointUnderEnvironmentVariables(Set.of())
                .build();

        var container = StubHelper.defaultStub(ConfluentKafkaContainer.class);
        var service = createKafkaService(config, container);

        // when
        service.shutdown();

        // then
        assertContainerStopped(container);
    }

    private KafkaDockerService createKafkaService(KafkaDockerServiceConfig config,
                                                  ConfluentKafkaContainer container) {
        var configurationRegistry = new DefaultConfigurationRegistry();
        var endpointRegisterer = new EndpointRegisterer(configurationRegistry);
        var topicAutoCreationConfigurer = new KafkaTopicAutoCreationConfigurer();

        return new KafkaDockerService(
                container,
                dockerNetwork,
                config,
                configurationRegistry,
                endpointRegisterer,
                networkConfigurer,
                topicConfigurer,
                topicAutoCreationConfigurer
        );
    }

    private void assertTopicsCreated(KafkaTopicConfigurer topicConfigurer,
                                     Set<KafkaTopic> topics) {
        topics.forEach(topic -> verify(topicConfigurer).configure(any(AdminClient.class), eq(topic)));
    }

    private void assertNoTopicsCreated(KafkaTopicConfigurer topicConfigurer) {
        verify(topicConfigurer, never()).configure(any(), any());
    }

}
