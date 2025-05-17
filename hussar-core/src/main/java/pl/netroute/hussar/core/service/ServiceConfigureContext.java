package pl.netroute.hussar.core.service;

import lombok.NonNull;
import pl.netroute.hussar.core.api.InternalUseOnly;
import pl.netroute.hussar.core.docker.api.DockerRegistry;
import pl.netroute.hussar.core.network.api.NetworkConfigurer;

@InternalUseOnly
public record ServiceConfigureContext(@NonNull DockerRegistry dockerRegistry,
                                      @NonNull NetworkConfigurer networkConfigurer) {

    public static ServiceConfigureContext defaultContext(@NonNull NetworkConfigurer networkConfigurer) {
        var dockerRegistry = DockerRegistry.defaultRegistry();

        return new ServiceConfigureContext(dockerRegistry, networkConfigurer);
    }

}
