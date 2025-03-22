package pl.netroute.hussar.service.rabbitmq.api;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class ManagementApiResolver {
    private static final String RABBITMQ_MANAGEMENT_VERSION = "management";

    static boolean isSupported(@NonNull RabbitMQDockerServiceConfig config) {
        var dockerImage = config.getDockerImage();

        return dockerImage.contains(RABBITMQ_MANAGEMENT_VERSION);
    }

}
