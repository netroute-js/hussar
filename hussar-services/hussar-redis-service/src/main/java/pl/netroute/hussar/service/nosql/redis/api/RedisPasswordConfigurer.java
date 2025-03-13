package pl.netroute.hussar.service.nosql.redis.api;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.testcontainers.containers.FixedHostPortGenericContainer;
import org.testcontainers.containers.GenericContainer;
import pl.netroute.hussar.core.api.InternalUseOnly;
import pl.netroute.hussar.core.docker.DockerCommandLineRunner;

import java.util.List;

@Slf4j
@InternalUseOnly
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class RedisPasswordConfigurer {
    private static final String CONFIGURE_PASSWORD_COMMAND = "redis-cli -h %s -p %d CONFIG SET requirepass %s";

    private final DockerCommandLineRunner commandLineRunner;

    void configure(@NonNull RedisCredentials credentials,
                   @NonNull GenericContainer<?> container) {
        var host = container.getHost();

        getPorts(container).forEach(port -> configureInstancePassword(host, port, credentials, container));
    }

    private void configureInstancePassword(String host,
                                           int port,
                                           RedisCredentials credentials,
                                           GenericContainer<?> container) {
        log.info("Configuring Redis[{}:{}] security credentials - {}", host, port, credentials);

        var password = credentials.password();
        var command = CONFIGURE_PASSWORD_COMMAND.formatted(host, port, password);

        commandLineRunner.run(command, container);
    }

    private List<Integer> getPorts(GenericContainer<?> container) {
        return switch(container) {
            case FixedHostPortGenericContainer fixedContainer -> fixedContainer.getBoundPortNumbers();
            default -> container.getExposedPorts();
        };
    }

}
