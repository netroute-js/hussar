package pl.netroute.hussar.service.kafka.api;

import lombok.NonNull;

public record KafkaTopic(@NonNull String name, int partitions) {
}
