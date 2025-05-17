package pl.netroute.hussar.service.kafka;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import pl.netroute.hussar.core.service.ServiceConfigureContext;
import pl.netroute.hussar.service.kafka.api.KafkaDockerService;
import pl.netroute.hussar.service.kafka.api.KafkaDockerServiceConfigurer;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class KafkaDockerServiceTestFactory {

    public static KafkaDockerService createMinimallyConfigured(@NonNull ServiceConfigureContext context) {
        return KafkaDockerServiceConfigurer
                .newInstance()
                .done()
                .configure(context);
    }

}
