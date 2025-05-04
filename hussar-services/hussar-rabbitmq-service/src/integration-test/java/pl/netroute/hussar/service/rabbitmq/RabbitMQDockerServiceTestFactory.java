package pl.netroute.hussar.service.rabbitmq;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import pl.netroute.hussar.core.service.ServiceConfigureContext;
import pl.netroute.hussar.service.rabbitmq.api.RabbitMQDockerService;
import pl.netroute.hussar.service.rabbitmq.api.RabbitMQDockerServiceConfigurer;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class RabbitMQDockerServiceTestFactory {

    public static RabbitMQDockerService createMinimallyConfigured(@NonNull String dockerImageVersion,
                                                                  @NonNull ServiceConfigureContext context) {
        return RabbitMQDockerServiceConfigurer
                .newInstance()
                .dockerImageVersion(dockerImageVersion)
                .done()
                .configure(context);
    }

}
