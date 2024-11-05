package pl.netroute.hussar.core.service.container;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * A custom GenericContainer factory.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GenericContainerFactory {

    /**
     * Factory method to create {@link GenericContainer}.
     *
     * @param dockerImage - the resolved {@link DockerImageName}
     * @return the instance of {@link GenericContainer}
     */
    public static GenericContainer<?> create(@NonNull DockerImageName dockerImage) {
        return new GenericContainer<>(dockerImage);
    }

}
