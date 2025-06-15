package pl.netroute.hussar.core.service.assertion;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.testcontainers.containers.FixedHostPortGenericContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.WaitStrategy;
import pl.netroute.hussar.core.docker.api.DockerNetwork;

import java.time.Duration;
import java.util.Map;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.matches;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GenericContainerAssertionHelper {
    private static final String DOCKER_ALIAS_REGEX = "^hussar-svc-[a-f0-9]{8}-[a-f0-9]{8}$";

    public static void assertContainerStarted(@NonNull GenericContainer<?> container) {
        verify(container).start();
    }

    public static void assertContainerStopped(@NonNull GenericContainer<?> container) {
        verify(container).stop();
    }

    public static void assertContainerExposedPortConfigured(@NonNull GenericContainer<?> container,
                                                            @NonNull Integer... exposedPorts) {
        if(container instanceof FixedHostPortGenericContainer fixedContainer) {
            Stream.of(exposedPorts)
                  .forEach(port -> fixedContainer.withFixedExposedPort(port, port));
        } else {
            Stream.of(exposedPorts)
                  .forEach(container::withExposedPorts);
        }
    }

    public static void assertContainerExtraHostConfigured(@NonNull GenericContainer<?> container,
                                                          @NonNull String hostName,
                                                          @NonNull String ipAddress) {
        verify(container).withExtraHost(hostName, ipAddress);
    }

    public static void assertNoContainerExtraHostConfigured(@NonNull GenericContainer<?> container,
                                                            @NonNull String hostName,
                                                            @NonNull String ipAddress) {
        verify(container, never()).withExtraHost(hostName, ipAddress);
    }

    public static void assertNoContainerExtraHostConfigured(@NonNull GenericContainer<?> container) {
        verify(container, never()).withExtraHost(anyString(), anyString());
    }

    public static void assertContainerWaitStrategyConfigured(@NonNull GenericContainer<?> container,
                                                             @NonNull WaitStrategy waitStrategy) {
        var waitStrategyType = waitStrategy.getClass();

        verify(container).waitingFor(any(waitStrategyType));
    }

    public static void assertContainerStartupTimeoutConfigured(@NonNull GenericContainer<?> container,
                                                               @NonNull Duration startupTimeout) {
        verify(container).withStartupTimeout(startupTimeout);
    }

    public static void assertContainerLoggingConfigured(@NonNull GenericContainer<?> container) {
        var loggingType = Slf4jLogConsumer.class;

        verify(container).withLogConsumer(any(loggingType));
    }

    public static void assertContainerEnvVariablesConfigured(@NonNull GenericContainer<?> container,
                                                             @NonNull Map<String, String> envVariables) {
        envVariables
                .forEach((envVariableName, envVariableValue) -> verify(container).withEnv(envVariableName, envVariableValue));
    }

    public static void assertContainerNoEnvVariablesConfigured(@NonNull GenericContainer<?> container) {
        verify(container, never()).withEnv(anyString(), anyString());
    }

    public static void assertContainerNetworkConfigured(@NonNull GenericContainer<?> container,
                                                        @NonNull DockerNetwork dockerNetwork) {
        verify(container).withNetwork(dockerNetwork.network());
        verify(container).withNetworkAliases(matches(DOCKER_ALIAS_REGEX));
    }

}
