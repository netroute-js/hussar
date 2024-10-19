package pl.netroute.hussar.service.rabbitmq.api;

import lombok.NonNull;

/**
 * A custom type representing RabbitMQ credentials.
 */
public record RabbitMQCredentials(@NonNull String username,
                                  @NonNull String password) {
}
