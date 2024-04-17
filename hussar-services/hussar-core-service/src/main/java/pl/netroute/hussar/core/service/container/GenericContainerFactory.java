package pl.netroute.hussar.core.service.container;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.testcontainers.containers.GenericContainer;
import pl.netroute.hussar.core.service.BaseDockerServiceConfig;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GenericContainerFactory {

    public static <T extends BaseDockerServiceConfig> GenericContainer<?> create(@NonNull T config) {
        return new GenericContainer<>(config.getDockerImage());
    }

}
