package pl.netroute.hussar.core.service;

import lombok.NonNull;
import pl.netroute.hussar.core.api.InternalUseOnly;
import pl.netroute.hussar.core.docker.api.DockerRegistry;

@InternalUseOnly
public record ServiceConfigureContext(@NonNull DockerRegistry dockerRegistry) {

    public static ServiceConfigureContext defaultContext() {
        var dockerRegistry = DockerRegistry.defaultRegistry();

        return new ServiceConfigureContext(dockerRegistry);
    }

}
