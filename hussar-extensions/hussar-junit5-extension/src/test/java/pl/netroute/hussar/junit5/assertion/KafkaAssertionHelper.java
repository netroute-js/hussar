package pl.netroute.hussar.junit5.assertion;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.TopicListing;
import org.apache.kafka.clients.producer.ProducerConfig;
import pl.netroute.hussar.core.api.Endpoint;
import pl.netroute.hussar.core.helper.EndpointHelper;
import pl.netroute.hussar.junit5.client.SimpleApplicationClient;
import pl.netroute.hussar.service.kafka.KafkaDockerService;
import pl.netroute.hussar.service.kafka.api.KafkaTopic;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.netroute.hussar.junit5.assertion.ApplicationPropertiesAssertionHelper.assertPropertyConfigured;
import static pl.netroute.hussar.junit5.config.ApplicationProperties.KAFKA_ALTERNATIVE_URL_PROPERTY;
import static pl.netroute.hussar.junit5.config.ApplicationProperties.KAFKA_URL_PROPERTY;
import static pl.netroute.hussar.junit5.factory.KafkaServiceFactory.KAFKA_EVENTS_TOPIC;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class KafkaAssertionHelper {
    private static final Duration KAFKA_TIMEOUT = Duration.ofSeconds(5L);

    public static void assertKafkaBootstrapped(@NonNull KafkaDockerService kafkaService,
                                               @NonNull SimpleApplicationClient applicationClient) {
        var endpoint = EndpointHelper.getAnyEndpointOrFail(kafkaService);

        assertKafkaReachable(endpoint);
        assertTopicsCreated(endpoint, List.of(KAFKA_EVENTS_TOPIC));
        assertPropertyConfigured(KAFKA_URL_PROPERTY, endpoint.address(), applicationClient);
        assertPropertyConfigured(KAFKA_ALTERNATIVE_URL_PROPERTY, endpoint.address(), applicationClient);
    }

    private static void assertKafkaReachable(Endpoint endpoint) {
        var client = createClient(endpoint);

        try {
            var nodes = client
                    .describeCluster()
                    .nodes()
                    .get(KAFKA_TIMEOUT.toMillis(), TimeUnit.MILLISECONDS);

            assertThat(nodes).isNotEmpty();
        } catch(Exception ex) {
            throw new IllegalStateException("Could not check Kafka accessibility", ex);
        }
    }

    private static void assertTopicsCreated(Endpoint endpoint,
                                            List<KafkaTopic> topics) {
        var actualTopicNames = listTopics(endpoint);

        var expectedTopicNames = topics
                .stream()
                .map(KafkaTopic::name)
                .toList();

        assertThat(actualTopicNames).containsExactlyInAnyOrderElementsOf(expectedTopicNames);
    }

    private static List<String> listTopics(Endpoint endpoint) {
        var client = createClient(endpoint);

        try {
            return client
                    .listTopics()
                    .listings()
                    .get(KAFKA_TIMEOUT.toMillis(), TimeUnit.MILLISECONDS)
                    .stream()
                    .filter(topic -> !topic.isInternal())
                    .map(TopicListing::name)
                    .toList();
        } catch(Exception ex) {
            throw new IllegalStateException("Could not list Kafka topics", ex);
        }
    }

    private static AdminClient createClient(Endpoint endpoint) {
        var connectionProperties = Map.<String, Object>of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, endpoint.address()
        );

        return AdminClient.create(connectionProperties);
    }
}
