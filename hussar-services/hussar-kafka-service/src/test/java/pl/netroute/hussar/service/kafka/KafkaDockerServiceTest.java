package pl.netroute.hussar.service.kafka;

import org.apache.kafka.clients.admin.AdminClient;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import pl.netroute.hussar.core.api.Endpoint;
import pl.netroute.hussar.core.configuration.api.ConfigurationEntry;
import pl.netroute.hussar.core.configuration.api.DefaultConfigurationRegistry;
import pl.netroute.hussar.core.service.api.ServiceStartupContext;
import pl.netroute.hussar.core.service.registerer.EndpointRegisterer;
import pl.netroute.hussar.service.kafka.api.KafkaTopic;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static pl.netroute.hussar.core.helper.SchemesHelper.EMPTY_SCHEME;
import static pl.netroute.hussar.core.service.assertion.GenericContainerAssertionHelper.assertContainerEnvVariablesConfigured;
import static pl.netroute.hussar.core.service.assertion.GenericContainerAssertionHelper.assertContainerExposedPortConfigured;
import static pl.netroute.hussar.core.service.assertion.GenericContainerAssertionHelper.assertContainerLoggingConfigured;
import static pl.netroute.hussar.core.service.assertion.GenericContainerAssertionHelper.assertContainerStarted;
import static pl.netroute.hussar.core.service.assertion.GenericContainerAssertionHelper.assertContainerStopped;
import static pl.netroute.hussar.core.service.assertion.GenericContainerAssertionHelper.assertContainerWaitStrategyConfigured;
import static pl.netroute.hussar.core.service.assertion.ServiceAssertionHelper.assertEntriesRegistered;
import static pl.netroute.hussar.core.service.assertion.ServiceAssertionHelper.assertName;
import static pl.netroute.hussar.core.service.assertion.ServiceAssertionHelper.assertNoEntriesRegistered;
import static pl.netroute.hussar.core.service.assertion.ServiceAssertionHelper.assertSingleEndpoint;

public class KafkaDockerServiceTest {
    private static final String KAFKA_HOST = "localhost";

    private static final int KAFKA_LISTENING_PORT = 9093;
    private static final int KAFKA_MAPPED_PORT = 19093;

    private static final String KAFKA_SERVICE_NAME = "kafka-service";
    private static final String KAFKA_SERVICE_IMAGE = "confluentinc/cp-kafka";

    private static final String KAFKA_LISTENERS_ENV = "KAFKA_LISTENERS";
    private static final String KAFKA_LISTENER_SECURITY_PROTOCOL_MAP_ENV = "KAFKA_LISTENER_SECURITY_PROTOCOL_MAP";
    private static final String KAFKA_INTER_BROKER_LISTENER_NAME_ENV = "KAFKA_INTER_BROKER_LISTENER_NAME";
    private static final String KAFKA_AUTO_CREATE_TOPICS_ENABLE_ENV = "KAFKA_AUTO_CREATE_TOPICS_ENABLE";

    private static final String KAFKA_LISTENERS = "BROKER://0.0.0.0:9092,EXTERNAL://0.0.0.0:9093";
    private static final String KAFKA_LISTENER_SECURITY_PROTOCOL_MAP = "BROKER:PLAINTEXT,EXTERNAL:PLAINTEXT";
    private static final String KAFKA_INTERNAL_LISTENER_NAME = "BROKER";

    private static final boolean KAFKA_AUTO_CREATE_TOPICS_DISABLED = false;
    private static final boolean KAFKA_AUTO_CREATE_TOPICS_ENABLED = true;

    @Test
    public void shouldStartMinimalService() {
        // given
        var config = KafkaDockerServiceConfig
                .builder()
                .name(KAFKA_SERVICE_NAME)
                .dockerImage(KAFKA_SERVICE_IMAGE)
                .scheme(EMPTY_SCHEME)
                .topics(Set.of())
                .registerEndpointUnderProperties(Set.of())
                .registerEndpointUnderEnvironmentVariables(Set.of())
                .build();

        var container = createStubContainer();
        var topicConfigurer = createTopicConfigurer();
        var kraftModeConfigurer = createKraftModeConfigurer();
        var service = createKafkaService(config, container, topicConfigurer, kraftModeConfigurer);

        givenContainerAccessible(container);

        // when
        service.start(ServiceStartupContext.empty());

        // then
        var endpoint = Endpoint.of(EMPTY_SCHEME, KAFKA_HOST, KAFKA_MAPPED_PORT);
        var envVariables = Map.of(
                KAFKA_LISTENERS_ENV, KAFKA_LISTENERS,
                KAFKA_LISTENER_SECURITY_PROTOCOL_MAP_ENV, KAFKA_LISTENER_SECURITY_PROTOCOL_MAP,
                KAFKA_INTER_BROKER_LISTENER_NAME_ENV, KAFKA_INTERNAL_LISTENER_NAME,
                KAFKA_AUTO_CREATE_TOPICS_ENABLE_ENV, KAFKA_AUTO_CREATE_TOPICS_DISABLED + ""
        );

        assertContainerStarted(container);
        assertContainerExposedPortConfigured(container, KAFKA_LISTENING_PORT);
        assertContainerWaitStrategyConfigured(container, Wait.forListeningPort());
        assertContainerLoggingConfigured(container);
        assertContainerEnvVariablesConfigured(container, envVariables);
        assertName(service, KAFKA_SERVICE_NAME);
        assertSingleEndpoint(service, endpoint);
        assertNoTopicsCreated(topicConfigurer);
        assertNoKraftModeConfigured(kraftModeConfigurer);
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
                .scheme(EMPTY_SCHEME)
                .topics(topics)
                .topicAutoCreation(true)
                .kraftMode(true)
                .registerEndpointUnderProperties(Set.of(endpointProperty))
                .registerEndpointUnderEnvironmentVariables(Set.of(endpointEnvVariable))
                .build();

        var container = createStubContainer();
        var topicConfigurer = createTopicConfigurer();
        var kraftModeConfigurer = createKraftModeConfigurer();
        var service = createKafkaService(config, container, topicConfigurer, kraftModeConfigurer);

        givenContainerAccessible(container);

        // when
        service.start(ServiceStartupContext.empty());

        // then
        var endpoint = Endpoint.of(EMPTY_SCHEME, KAFKA_HOST, KAFKA_MAPPED_PORT);
        var endpointPropertyEntry = ConfigurationEntry.property(endpointProperty, endpoint.address());
        var endpointEnvVariableEntry = ConfigurationEntry.envVariable(endpointEnvVariable, endpoint.address());

        var registeredEntries = List.<ConfigurationEntry>of(
                endpointPropertyEntry,
                endpointEnvVariableEntry
        );

        var envVariables = Map.of(
                KAFKA_LISTENERS_ENV, KAFKA_LISTENERS,
                KAFKA_LISTENER_SECURITY_PROTOCOL_MAP_ENV, KAFKA_LISTENER_SECURITY_PROTOCOL_MAP,
                KAFKA_INTER_BROKER_LISTENER_NAME_ENV, KAFKA_INTERNAL_LISTENER_NAME,
                KAFKA_AUTO_CREATE_TOPICS_ENABLE_ENV, KAFKA_AUTO_CREATE_TOPICS_ENABLED + ""
        );

        assertContainerStarted(container);
        assertContainerExposedPortConfigured(container, KAFKA_LISTENING_PORT);
        assertContainerWaitStrategyConfigured(container, Wait.forListeningPort());
        assertContainerLoggingConfigured(container);
        assertContainerEnvVariablesConfigured(container, envVariables);
        assertName(service, KAFKA_SERVICE_NAME);
        assertSingleEndpoint(service, endpoint);
        assertTopicsCreated(topicConfigurer, topics);
        assertKraftModeConfigured(kraftModeConfigurer, container);
        assertEntriesRegistered(service, registeredEntries);
    }

    @Test
    public void shouldShutdownService() {
        // given
        var config = KafkaDockerServiceConfig
                .builder()
                .name(KAFKA_SERVICE_NAME)
                .dockerImage(KAFKA_SERVICE_IMAGE)
                .scheme(EMPTY_SCHEME)
                .topics(Set.of())
                .registerEndpointUnderProperties(Set.of())
                .registerEndpointUnderEnvironmentVariables(Set.of())
                .build();

        var container = createStubContainer();
        var topicConfigurer = createTopicConfigurer();
        var kraftModeConfigurer = createKraftModeConfigurer();
        var service = createKafkaService(config, container, topicConfigurer, kraftModeConfigurer);

        givenContainerAccessible(container);

        // when
        service.start(ServiceStartupContext.empty());
        service.shutdown();

        // then
        assertContainerStopped(container);
    }

    private KafkaDockerService createKafkaService(KafkaDockerServiceConfig config,
                                                  KafkaContainer container,
                                                  KafkaTopicConfigurer topicConfigurer,
                                                  KafkaKraftModeConfigurer kraftModeConfigurer) {
        var configurationRegistry = new DefaultConfigurationRegistry();
        var endpointRegisterer = new EndpointRegisterer(configurationRegistry);
        var listenerConfigurer = new KafkaListenerConfigurer();
        var topicAutoCreationConfigurer = new KafkaTopicAutoCreationConfigurer();

        return new KafkaDockerService(
                container,
                config,
                configurationRegistry,
                endpointRegisterer,
                listenerConfigurer,
                topicConfigurer,
                topicAutoCreationConfigurer,
                kraftModeConfigurer
        );
    }

    private KafkaContainer createStubContainer() {
        return mock(KafkaContainer.class, RETURNS_DEEP_STUBS);
    }

    private KafkaTopicConfigurer createTopicConfigurer() {
        return mock(KafkaTopicConfigurer.class, RETURNS_DEEP_STUBS);
    }

    private KafkaKraftModeConfigurer createKraftModeConfigurer() {
        return mock(KafkaKraftModeConfigurer.class, RETURNS_DEEP_STUBS);
    }

    private void givenContainerAccessible(KafkaContainer container) {
        when(container.getHost()).thenReturn(KAFKA_HOST);
        when(container.getExposedPorts()).thenReturn(List.of(KAFKA_LISTENING_PORT));
        when(container.getMappedPort(KAFKA_LISTENING_PORT)).thenReturn(KAFKA_MAPPED_PORT);
    }

    private void assertTopicsCreated(KafkaTopicConfigurer topicConfigurer,
                                     Set<KafkaTopic> topics) {
        topics.forEach(topic -> verify(topicConfigurer).configure(any(AdminClient.class), eq(topic)));
    }

    private void assertNoTopicsCreated(KafkaTopicConfigurer topicConfigurer) {
        verify(topicConfigurer, never()).configure(any(), any());
    }

    private void assertKraftModeConfigured(KafkaKraftModeConfigurer kraftModeConfigurer,
                                           KafkaContainer container) {
        verify(kraftModeConfigurer).configure(container);
    }

    private void assertNoKraftModeConfigured(KafkaKraftModeConfigurer kraftModeConfigurer) {
        verify(kraftModeConfigurer, never()).configure(any());
    }
}
