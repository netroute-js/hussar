package pl.netroute.hussar.core.docker.api;

import lombok.NonNull;
import org.testcontainers.containers.Network;

public record DockerNetwork(@NonNull Network network) {

    public static DockerNetwork newNetwork() {
        var network = Network.newNetwork();

        return new DockerNetwork(network);
    }

}
