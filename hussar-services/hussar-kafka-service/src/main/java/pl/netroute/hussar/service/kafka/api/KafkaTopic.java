package pl.netroute.hussar.service.kafka.api;

import lombok.Builder;
import lombok.NonNull;

/**
 * A custom type that represents Kafka topic.
 */
@Builder
public record KafkaTopic(@NonNull String name, int partitions) {
}
