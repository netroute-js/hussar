package pl.netroute.hussar.core.service.container;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.testcontainers.containers.GenericContainer;
import pl.netroute.hussar.core.service.BaseDockerServiceConfig;

/**
 * A custom GenericContainer factory.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GenericContainerFactory {

    /**
     * Factory method to create {@link GenericContainer}.
     *
     * @param <T> - the subtype of {@link BaseDockerServiceConfig}.
     * @param config - the config of {@link BaseDockerServiceConfig}
     * @return the instance of {@link GenericContainer}
     */
    public static <T extends BaseDockerServiceConfig> GenericContainer<?> create(@NonNull T config) {
        return new GenericContainer<>(config.getDockerImage());
    }

}
