package pl.netroute.hussar.core.docker;

import lombok.NoArgsConstructor;
import org.testcontainers.DockerClientFactory;
import pl.netroute.hussar.core.api.InternalUseOnly;
import pl.netroute.hussar.core.helper.IpHelper;

import java.util.List;

@InternalUseOnly
@NoArgsConstructor
public class DockerHostResolver {
    private static final String DOCKER_BRIDGE_NETWORK = "bridge";

    private static final String OPERATION_SYSTEM_PROPERTY = "os.name";
    private static final String LINUX_OPERATION_SYSTEM = "linux";

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

    public String getGatewayHost() {
        if(isLinuxEnvironment()) {
            return DockerClientFactory
                    .instance()
                    .client()
                    .inspectNetworkCmd()
                    .withNetworkId(DOCKER_BRIDGE_NETWORK)
                    .exec()
                    .getIpam()
                    .getConfig()
                    .getFirst()
                    .getGateway();
        }

        return IpHelper.getRoutableIP();
    }

    private boolean isLinuxEnvironment() {
        return System
                .getProperty(OPERATION_SYSTEM_PROPERTY)
                .toLowerCase()
                .contains(LINUX_OPERATION_SYSTEM);
    }

}
