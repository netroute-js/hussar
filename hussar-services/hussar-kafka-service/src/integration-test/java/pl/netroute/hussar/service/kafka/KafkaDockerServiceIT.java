package pl.netroute.hussar.service.kafka;

import org.junit.jupiter.api.Test;
import pl.netroute.hussar.core.api.Endpoint;
import pl.netroute.hussar.core.service.BaseServiceIT;
import pl.netroute.hussar.core.service.ServiceConfigureContext;
import pl.netroute.hussar.core.service.ServiceStartupContext;
import pl.netroute.hussar.service.kafka.api.KafkaDockerService;
import pl.netroute.hussar.service.kafka.api.KafkaDockerServiceConfigurer;
import pl.netroute.hussar.service.kafka.api.KafkaTopic;
import pl.netroute.hussar.service.kafka.assertion.KafkaAssertionHelper;
import pl.netroute.hussar.service.kafka.helper.KafkaSenderFactory;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class KafkaDockerServiceIT extends BaseServiceIT<KafkaDockerService> {

    @Test
    public void shouldAutoCreateTopic() {
        // given
        var partitions = 1;
        var topic = new KafkaTopic("topic", partitions);
        var message = "a-message";
        var context = ServiceConfigureContext.defaultContext(networkOperator.getNetworkConfigurer());

        service = KafkaDockerServiceConfigurer
                .newInstance()
                .topicAutoCreation(true)
                .done()
                .configure(context);

        // when
        service.start(ServiceStartupContext.defaultContext());

        KafkaSenderFactory
                .create(service)
                .send(topic.name(), message);

        // then
        var kafkaAssertion = new KafkaAssertionHelper(service);
        kafkaAssertion.assertSingleEndpoint();
        kafkaAssertion.asserKafkaAccessible();
        kafkaAssertion.assertTopicsCreated(List.of(topic));
    }


    @Test
    public void shouldFailStartingKafkaServiceWhenKraftModeNotSupported() {
        // given
        var dockerVersion = "3.0.0";
        var context = ServiceConfigureContext.defaultContext(networkOperator.getNetworkConfigurer());

        service = KafkaDockerServiceConfigurer
                .newInstance()
                .dockerImageVersion(dockerVersion)
                .kraftMode(true)
                .done()
                .configure(context);

        // when
        // then
        assertThatThrownBy(() -> service.start(ServiceStartupContext.defaultContext()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Provided Confluent Platform's version 3.0.0 is not supported in Kraft mode (must be 7.0.0 or above)");
    }

    @Override
    protected ServiceTestMetadata<KafkaDockerService, Consumer<KafkaDockerService>> provideMinimallyConfiguredServiceTestMetadata(ServiceConfigureContext context) {
        var service = KafkaDockerServiceConfigurer
                .newInstance()
                .done()
                .configure(context);

        var assertion = (Consumer<KafkaDockerService>) actualService -> {
            var kafkaAssertion = new KafkaAssertionHelper(actualService);
            kafkaAssertion.assertSingleEndpoint();
            kafkaAssertion.asserKafkaAccessible();
            kafkaAssertion.assertNoTopicsCreated();
            kafkaAssertion.assertNoEntriesRegistered();
        };

        return ServiceTestMetadata
                .<KafkaDockerService, Consumer<KafkaDockerService>>newInstance()
                .service(service)
                .assertion(assertion)
                .done();
    }

    @Override
    protected ServiceTestMetadata<KafkaDockerService, Consumer<KafkaDockerService>> provideFullyConfiguredServiceTestMetadata(ServiceConfigureContext context) {
        var name = "kafka-instance";
        var dockerVersion = "7.5.4";

        var partitions = 5;
        var topicA = new KafkaTopic("topicA", partitions);
        var topicB = new KafkaTopic("topicB", partitions);

        var endpointProperty = "kafka.url";
        var endpointEnvVariable = "KAFKA_URL";

        var service = KafkaDockerServiceConfigurer
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
                .configure(context);

        var assertion = (Consumer<KafkaDockerService>) actualService -> {
            var kafkaAssertion = new KafkaAssertionHelper(actualService);
            kafkaAssertion.assertSingleEndpoint();
            kafkaAssertion.asserKafkaAccessible();
            kafkaAssertion.assertTopicsCreated(List.of(topicA, topicB));
            kafkaAssertion.assertRegisteredEndpointUnderProperty(endpointProperty);
            kafkaAssertion.assertRegisteredEndpointUnderEnvironmentVariable(endpointEnvVariable);
        };

        return ServiceTestMetadata
                .<KafkaDockerService, Consumer<KafkaDockerService>>newInstance()
                .service(service)
                .assertion(assertion)
                .done();
    }

    @Override
    protected ServiceTestMetadata<KafkaDockerService, BiConsumer<KafkaDockerService, List<Endpoint>>> provideShutdownServiceTestMetadata(ServiceConfigureContext context) {
        var service = KafkaDockerServiceConfigurer
                .newInstance()
                .done()
                .configure(context);

        var assertion = (BiConsumer<KafkaDockerService, List<Endpoint>>) (actualService, endpoints) -> {
            var endpoint = endpoints.getFirst();

            var kafkaAssertion = new KafkaAssertionHelper(actualService);
            kafkaAssertion.assertKafkaNotAccessible(endpoint);
        };

        return ServiceTestMetadata
                .<KafkaDockerService, BiConsumer<KafkaDockerService, List<Endpoint>>>newInstance()
                .service(service)
                .assertion(assertion)
                .done();
    }

}
