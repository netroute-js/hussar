package pl.netroute.hussar.service.nosql.redis.api;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.testcontainers.containers.GenericContainer;
import pl.netroute.hussar.core.api.InternalUseOnly;
import pl.netroute.hussar.core.docker.DockerCommandLineRunner;

@Slf4j
@InternalUseOnly
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class RedisClusterAnnounceIpConfigurer {
    private static final String CONFIGURE_CLUSTER_ANNOUNCE_IP_COMMAND = "redis-cli -h %s -p %d CONFIG SET cluster-announce-ip %s";

    private final DockerCommandLineRunner commandLineRunner;

    void configure(@NonNull String clusterAnnounceIp,
                   @NonNull GenericContainer<?> container) {
        var host = container.getHost();

        container
                .getExposedPorts()
                .forEach(port -> configureInstanceClusterAnnounceIp(host, port, clusterAnnounceIp, container));
    }

    private void configureInstanceClusterAnnounceIp(String host,
                                                    int port,
                                                    String clusterAnnounceIp,
                                                    GenericContainer<?> container) {
        log.info("Configuring Redis[{}:{}] cluster announce IP to {}", host, port, clusterAnnounceIp);

        var command = CONFIGURE_CLUSTER_ANNOUNCE_IP_COMMAND.formatted(host, port, clusterAnnounceIp);

        commandLineRunner.run(command, container);
    }

}
