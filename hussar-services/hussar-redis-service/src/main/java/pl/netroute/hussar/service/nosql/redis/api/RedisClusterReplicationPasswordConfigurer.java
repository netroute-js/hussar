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
class RedisClusterReplicationPasswordConfigurer {
    private static final String CONFIGURE_PASSWORD_REPLICATION_COMMAND = "redis-cli -h %s -p %d CONFIG SET masterauth %s";

    private final DockerCommandLineRunner commandLineRunner;

    void configure(@NonNull RedisCredentials credentials,
                   @NonNull GenericContainer<?> container) {
        var host = container.getHost();

        container
                .getExposedPorts()
                .forEach(port -> configureInstanceReplicationPassword(host, port, credentials, container));
    }

    private void configureInstanceReplicationPassword(String host,
                                                      int port,
                                                      RedisCredentials credentials,
                                                      GenericContainer<?> container) {
        log.info("Configuring Redis[{}:{}] replication password to {}", host, port, credentials);

        var password = credentials.password();
        var command = CONFIGURE_PASSWORD_REPLICATION_COMMAND.formatted(host, port, password);

        commandLineRunner.run(command, container);
    }

}
