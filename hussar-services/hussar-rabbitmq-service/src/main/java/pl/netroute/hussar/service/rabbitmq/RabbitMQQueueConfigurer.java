package pl.netroute.hussar.service.rabbitmq;

import com.rabbitmq.client.ConnectionFactory;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import pl.netroute.hussar.service.rabbitmq.api.RabbitMQQueue;

@Slf4j
class RabbitMQQueueConfigurer {

    void configure(@NonNull ConnectionFactory connectionFactory,
                   @NonNull RabbitMQQueue queue) {
        log.info("Creating {}", queue);

        try(var connection = connectionFactory.newConnection();
            var channel = connection.createChannel()) {

            var name = queue.name();
            var durable = queue.durable();
            var exclusive = queue.exclusive();
            var autoDelete = queue.autoDelete();
            var arguments = queue.arguments();

            channel.queueDeclare(name, durable, exclusive, autoDelete, arguments);
        } catch (Exception ex) {
            throw new IllegalStateException("Could not create RabbitMQ queue", ex);
        }
    }

}
