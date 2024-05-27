package pl.netroute.hussar.service.rabbitmq.api;

import lombok.NonNull;

import java.util.Map;

public record RabbitMQQueue(@NonNull String name,
                            boolean durable,
                            boolean exclusive,
                            boolean autoDelete,
                            @NonNull Map<String, Object> arguments) {
}
