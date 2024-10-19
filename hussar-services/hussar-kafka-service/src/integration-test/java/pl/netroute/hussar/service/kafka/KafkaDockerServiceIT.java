package pl.netroute.hussar.service.kafka;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import pl.netroute.hussar.core.api.service.ServiceStartupContext;
import pl.netroute.hussar.core.helper.EndpointHelper;
import pl.netroute.hussar.service.kafka.api.KafkaTopic;
import pl.netroute.hussar.service.kafka.assertion.KafkaAssertionHelper;
import pl.netroute.hussar.service.kafka.helper.KafkaSenderFactory;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class KafkaDockerServiceIT {
    private KafkaDockerService kafkaService;

    @AfterEach
    public void cleanup() {
        Optional
                .ofNullable(kafkaService)
                .ifPresent(KafkaDockerService::shutdown);
    }

    @Test
    public void shouldStartKafkaService() {
        // given
        kafkaService = KafkaDockerServiceConfigurer
                .newInstance()
                .done()
                .configure();

        // when
        kafkaService.start(ServiceStartupContext.empty());

        // then
        var kafkaAssertion = new KafkaAssertionHelper(kafkaService);
        kafkaAssertion.assertSingleEndpoint();
        kafkaAssertion.asserKafkaAccessible();
        kafkaAssertion.assertNoTopicsCreated();
        kafkaAssertion.assertNoEntriesRegistered();
    }

    @Test
    public void shouldStartExtendedKafkaService() {
        // given
        var name = "kafka-instance";
        var dockerVersion = "7.5.4";

        var partitions = 5;
        var topicA = new KafkaTopic("topicA", partitions);
        var topicB = new KafkaTopic("topicB", partitions);

        var endpointProperty = "kafka.url";
        var endpointEnvVariable = "KAFKA_URL";

        kafkaService = KafkaDockerServiceConfigurer
                .newInstance()
                .name(name)
                .topic(topicA)
                .topic(topicB)
                .topicAutoCreation(true)
                .kraftMode(true)
                .dockerImageVersion(dockerVersion)
                .registerEndpointUnderProperty(endpointProperty)
                .registerEndpointUnderEnvironmentVariable(endpointEnvVariable)
                .done()
                .configure();

        // when
        kafkaService.start(ServiceStartupContext.empty());

        // then
        var kafkaAssertion = new KafkaAssertionHelper(kafkaService);
        kafkaAssertion.assertSingleEndpoint();
        kafkaAssertion.asserKafkaAccessible();
        kafkaAssertion.assertTopicsCreated(List.of(topicA, topicB));
        kafkaAssertion.assertRegisteredEndpointUnderProperty(endpointProperty);
        kafkaAssertion.assertRegisteredEndpointUnderEnvironmentVariable(endpointEnvVariable);
    }

    @Test
    public void shouldAutoCreateTopic() {
        // given
        var name = "kafka-instance";
        var dockerVersion = "7.5.4";

        var partitions = 1;
        var topic = new KafkaTopic("topic", partitions);

        var endpointProperty = "kafka.url";
        var endpointEnvVariable = "KAFKA_URL";

        var message = "a-message";

        kafkaService = KafkaDockerServiceConfigurer
                .newInstance()
                .name(name)
                .topicAutoCreation(true)
                .kraftMode(true)
                .dockerImageVersion(dockerVersion)
                .registerEndpointUnderProperty(endpointProperty)
                .registerEndpointUnderEnvironmentVariable(endpointEnvVariable)
                .done()
                .configure();

        kafkaService.start(ServiceStartupContext.empty());

        // when
        KafkaSenderFactory
                .create(kafkaService)
                .send(topic.name(), message);

        // then
        var kafkaAssertion = new KafkaAssertionHelper(kafkaService);
        kafkaAssertion.assertSingleEndpoint();
        kafkaAssertion.asserKafkaAccessible();
        kafkaAssertion.assertTopicsCreated(List.of(topic));
        kafkaAssertion.assertRegisteredEndpointUnderProperty(endpointProperty);
        kafkaAssertion.assertRegisteredEndpointUnderEnvironmentVariable(endpointEnvVariable);
    }

    @Test
    public void shouldFailAutoCreatingTopicWhenFeatureDisabled() {
        // given
        var topic = "topic";
        var message = "a-message";

        kafkaService = KafkaDockerServiceConfigurer
                .newInstance()
                .topicAutoCreation(false)
                .done()
                .configure();

        kafkaService.start(ServiceStartupContext.empty());

        // when
        // then
        var sender = KafkaSenderFactory.create(kafkaService);

        assertThatThrownBy(() -> sender.send(topic, message))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Could not send record")
                .hasRootCauseMessage("Topic topic not present in metadata after 5000 ms.");

        var kafkaAssertion = new KafkaAssertionHelper(kafkaService);
        kafkaAssertion.assertSingleEndpoint();
        kafkaAssertion.asserKafkaAccessible();
        kafkaAssertion.assertNoTopicsCreated();
        kafkaAssertion.assertNoEntriesRegistered();
    }

    @Test
    public void shouldFailStartingKafkaServiceWhenKraftModeNotSupported() {
        // given
        var dockerVersion = "3.0.0";

        kafkaService = KafkaDockerServiceConfigurer
                .newInstance()
                .dockerImageVersion(dockerVersion)
                .kraftMode(true)
                .done()
                .configure();

        // when
        // then
        assertThatThrownBy(() -> kafkaService.start(ServiceStartupContext.empty()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Provided Confluent Platform's version 3.0.0 is not supported in Kraft mode (must be 7.0.0 or above)");
    }

    @Test
    public void shouldShutdownKafkaService() {
        var name = "kafka-instance";
        var dockerVersion = "7.5.4";

        kafkaService = KafkaDockerServiceConfigurer
                .newInstance()
                .name(name)
                .dockerImageVersion(dockerVersion)
                .done()
                .configure();

        // when
        kafkaService.start(ServiceStartupContext.empty());

        var endpoint = EndpointHelper.getAnyEndpointOrFail(kafkaService);

        kafkaService.shutdown();

        // then
        var databaseAssertion = new KafkaAssertionHelper(kafkaService);
        databaseAssertion.asserKafkaNotAccessible(endpoint);
    }
}
