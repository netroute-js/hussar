package pl.netroute.hussar.service.nosql.redis;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.testcontainers.containers.GenericContainer;
import pl.netroute.hussar.service.nosql.redis.api.RedisCredentials;

@Slf4j
@RequiredArgsConstructor
class RedisPasswordConfigurer {
    private static final String CONFIGURE_PASSWORD_COMMAND = "redis-cli -h %s -p %d config set requirepass %s";
    private static final String COMMAND_SPLITTER = " ";

    void configure(@NonNull RedisCredentials credentials,
                   @NonNull GenericContainer<?> container) {
        container
                .getExposedPorts()
                .forEach(port -> configureInstancePassword(port, credentials, container));
    }

    private void configureInstancePassword(int port,
                                           RedisCredentials credentials,
                                           GenericContainer<?> container) {
        log.info("Configuring Redis[{}] credentials - {}", port, credentials);

        var host = container.getHost();
        var password = credentials.password();
        var command = CONFIGURE_PASSWORD_COMMAND
                .formatted(host, port, password)
                .split(COMMAND_SPLITTER);

        try {
            var result = container.execInContainer(command);
            if(result.getExitCode() != 0) {
                var errorMessage = "Failed to configure Redis[%d] credentials - %s".formatted(port, result.getStderr());

                throw new IllegalStateException(errorMessage);
            }
        } catch (Exception ex) {
            throw new IllegalStateException("Could not configure Redis credentials", ex);
        }
    }

}
