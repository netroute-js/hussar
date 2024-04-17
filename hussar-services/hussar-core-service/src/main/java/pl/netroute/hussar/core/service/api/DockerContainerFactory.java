package pl.netroute.hussar.core.service.api;

import lombok.NonNull;
import org.testcontainers.containers.GenericContainer;

public interface DockerContainerFactory {
    GenericContainer<?> create(@NonNull String dockerImage);
}
