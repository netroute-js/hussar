package pl.netroute.hussar.service.kafka.api;

import lombok.Builder;
import lombok.NonNull;

@Builder
public record KafkaTopic(@NonNull String name, int partitions) {
}
