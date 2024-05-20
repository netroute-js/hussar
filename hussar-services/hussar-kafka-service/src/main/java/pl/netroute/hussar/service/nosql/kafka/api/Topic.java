package pl.netroute.hussar.service.nosql.kafka.api;

import lombok.NonNull;

public record Topic(@NonNull String name, int partitions) {
}
