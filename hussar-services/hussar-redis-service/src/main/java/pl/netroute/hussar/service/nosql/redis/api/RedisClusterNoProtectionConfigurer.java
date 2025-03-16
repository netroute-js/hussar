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
class RedisClusterNoProtectionConfigurer {
    private static final String DISABLE_PROTECTION_MODE_COMMAND = "redis-cli -h %s -p %d CONFIG SET protection-mode no";

    private final DockerCommandLineRunner commandLineRunner;

    void configure(@NonNull GenericContainer<?> container) {
        var host = container.getHost();

        container
                .getBoundPortNumbers()
                .forEach(port -> disableClusterInstanceProtectionMode(host, port, container));
    }

    private void disableClusterInstanceProtectionMode(String host,
                                                      int port,
                                                      GenericContainer<?> container) {
        log.info("Disabling Redis[{}:{}] protection mode", host, port);

        var command = DISABLE_PROTECTION_MODE_COMMAND.formatted(host, port);

        commandLineRunner.run(command, container);
    }

}
