package pl.netroute.hussar.service.rabbitmq.api;

import lombok.NonNull;

public record RabbitMQCredentials(@NonNull String username,
                                  @NonNull String password) {
}
