package pl.netroute.hussar.core.docker;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;
import pl.netroute.hussar.core.api.InternalUseOnly;

/**
 * A custom GenericContainer factory.
 */
@InternalUseOnly
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
