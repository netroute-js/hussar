package pl.netroute.hussar.junit5.factory;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.netroute.hussar.service.rabbitmq.api.RabbitMQDockerServiceConfigurer;
import pl.netroute.hussar.service.rabbitmq.api.RabbitMQQueue;

import static pl.netroute.hussar.junit5.config.ApplicationEnvironmentVariables.RABBITMQ_PASSWORD_ENV_VARIABLE;
import static pl.netroute.hussar.junit5.config.ApplicationEnvironmentVariables.RABBITMQ_URL_ENV_VARIABLE;
import static pl.netroute.hussar.junit5.config.ApplicationEnvironmentVariables.RABBITMQ_USERNAME_ENV_VARIABLE;
import static pl.netroute.hussar.junit5.config.ApplicationProperties.RABBITMQ_ALTERNATIVE_PASSWORD_PROPERTY;
import static pl.netroute.hussar.junit5.config.ApplicationProperties.RABBITMQ_ALTERNATIVE_URL_PROPERTY;
import static pl.netroute.hussar.junit5.config.ApplicationProperties.RABBITMQ_ALTERNATIVE_USERNAME_PROPERTY;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RabbitMQServiceFactory {
    public static final String RABBITMQ_NAME = "rabbitmq-instance";

    public static final RabbitMQQueue RABBITMQ_EVENTS_QUEUE = RabbitMQQueue
            .builder()
            .name("hussar.events.stream")
            .build();

    public static RabbitMQDockerServiceConfigurer create() {
        var dockerImageVersion = "3.13.3-management-alpine";

        return RabbitMQDockerServiceConfigurer
                .newInstance()
                .name(RABBITMQ_NAME)
                .dockerImageVersion(dockerImageVersion)
                .queue(RABBITMQ_EVENTS_QUEUE)
                .registerEndpointUnderProperty(RABBITMQ_ALTERNATIVE_URL_PROPERTY)
                .registerEndpointUnderEnvironmentVariable(RABBITMQ_URL_ENV_VARIABLE)
                .registerUsernameUnderProperty(RABBITMQ_ALTERNATIVE_USERNAME_PROPERTY)
                .registerUsernameUnderEnvironmentVariable(RABBITMQ_USERNAME_ENV_VARIABLE)
                .registerPasswordUnderProperty(RABBITMQ_ALTERNATIVE_PASSWORD_PROPERTY)
                .registerPasswordUnderEnvironmentVariable(RABBITMQ_PASSWORD_ENV_VARIABLE)
                .done();
    }

}
