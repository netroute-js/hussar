package pl.netroute.hussar.service.rabbitmq.api;

import lombok.Builder;
import lombok.NonNull;

import java.util.Map;

@Builder
public record RabbitMQQueue(@NonNull String name,
                            boolean durable,
                            boolean exclusive,
                            boolean autoDelete,
                            Map<String, Object> arguments) {
}
