package pl.netroute.hussar.core.docker;

import lombok.NoArgsConstructor;
import org.testcontainers.DockerClientFactory;
import pl.netroute.hussar.core.api.InternalUseOnly;

import java.util.List;

@InternalUseOnly
@NoArgsConstructor
public class DockerHostResolver {
    public static final String DOCKER_BRIDGE_HOST = "host.docker.internal";
    public static final String DOCKER_HOST_GATEWAY = "host-gateway";

    private static final List<String> LOCALHOST_ADDRESSES = List.of("localhost", "127.0.0.1");

    public boolean isLocalHost() {
        var hostAddress = getHost();

        return LOCALHOST_ADDRESSES.contains(hostAddress);
    }

    public String getHost() {
        return DockerClientFactory
                .instance()
                .dockerHostIpAddress();
    }

}
