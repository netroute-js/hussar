package pl.netroute.hussar.core.service;

import lombok.NonNull;
import pl.netroute.hussar.core.api.InternalUseOnly;
import pl.netroute.hussar.core.docker.api.DockerNetwork;
import pl.netroute.hussar.core.docker.api.DockerRegistry;
import pl.netroute.hussar.core.network.api.NetworkConfigurer;

@InternalUseOnly
public record ServiceConfigureContext(@NonNull DockerRegistry dockerRegistry,
                                      @NonNull DockerNetwork dockerNetwork,
                                      @NonNull NetworkConfigurer networkConfigurer) {

    public static ServiceConfigureContext defaultContext(@NonNull DockerNetwork dockerNetwork,
                                                         @NonNull NetworkConfigurer networkConfigurer) {
        var dockerRegistry = DockerRegistry.defaultRegistry();

        return new ServiceConfigureContext(dockerRegistry, dockerNetwork, networkConfigurer);
    }

}
