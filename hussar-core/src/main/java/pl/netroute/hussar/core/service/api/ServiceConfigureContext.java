package pl.netroute.hussar.core.service.api;

import lombok.NonNull;
import pl.netroute.hussar.core.docker.api.DockerRegistry;

public record ServiceConfigureContext(@NonNull DockerRegistry dockerRegistry) {

    public static ServiceConfigureContext defaultContext() {
        var dockerRegistry = DockerRegistry.defaultRegistry();

        return new ServiceConfigureContext(dockerRegistry);
    }

}
