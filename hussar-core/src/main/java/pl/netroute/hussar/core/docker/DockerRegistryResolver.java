package pl.netroute.hussar.core.docker;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import pl.netroute.hussar.core.api.InternalUseOnly;
import pl.netroute.hussar.core.docker.api.DockerRegistry;
import pl.netroute.hussar.core.service.ServiceConfigureContext;

@InternalUseOnly
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DockerRegistryResolver {

    public static DockerRegistry resolve(DockerRegistry overriddenRegistry,
                                         @NonNull ServiceConfigureContext serviceContext) {
        return overriddenRegistry != null ? overriddenRegistry : serviceContext.dockerRegistry();
    }

}
