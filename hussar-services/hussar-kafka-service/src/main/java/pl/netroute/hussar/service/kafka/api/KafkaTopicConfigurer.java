package pl.netroute.hussar.service.kafka.api;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import pl.netroute.hussar.core.api.InternalUseOnly;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@InternalUseOnly
@NoArgsConstructor(access = AccessLevel.PACKAGE)
class KafkaTopicConfigurer {
    private static final short REPLICATION_FACTOR = 1;

    private static final Duration KAFKA_TIMEOUT = Duration.ofSeconds(5L);

    void configure(@NonNull AdminClient adminClient,
                   @NonNull KafkaTopic topic) {
        log.info("Creating {}", topic);

        var newTopic = mapToNewTopic(topic);

        try {
            adminClient
                    .createTopics(List.of(newTopic))
                    .all()
                    .get(KAFKA_TIMEOUT.toMillis(), TimeUnit.MILLISECONDS);
        } catch (Exception ex) {
            throw new IllegalStateException("Could not create Kafka topic", ex);
        }
    }

    private NewTopic mapToNewTopic(KafkaTopic topic) {
        return new NewTopic(topic.name(), topic.partitions(), REPLICATION_FACTOR);
    }

}
