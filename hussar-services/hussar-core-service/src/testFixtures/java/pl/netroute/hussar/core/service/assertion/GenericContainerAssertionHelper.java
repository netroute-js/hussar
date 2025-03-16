package pl.netroute.hussar.core.service.assertion;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.testcontainers.containers.FixedHostPortGenericContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.WaitStrategy;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GenericContainerAssertionHelper {
    private static final String COMMAND_SPLITTER = " ";

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
            verify(container).withExposedPorts(exposedPorts);
        }
    }

    public static void assertContainerExtraHostConfigured(@NonNull GenericContainer<?> container,
                                                          @NonNull String hostName,
                                                          @NonNull String ipAddress) {
        verify(container).withExtraHost(hostName, ipAddress);
    }

    public static void assertNoContainerExtraHostConfigured(@NonNull GenericContainer<?> container) {
        verify(container, never()).withExtraHost(anyString(), anyString());
    }

    public static void assertContainerWaitStrategyConfigured(@NonNull GenericContainer<?> container,
                                                             @NonNull WaitStrategy waitStrategy) {
        var waitStrategyType = waitStrategy.getClass();

        verify(container).waitingFor(any(waitStrategyType));
    }

    public static void assertContainerLoggingConfigured(@NonNull GenericContainer<?> container) {
        var loggingType = Slf4jLogConsumer.class;

        verify(container).withLogConsumer(any(loggingType));
    }

    public static void assertContainerEnvVariablesConfigured(@NonNull GenericContainer<?> container,
                                                             @NonNull Map<String, String> envVariables) {
        envVariables
                .entrySet()
                .forEach(envVariable -> verify(container).withEnv(envVariable.getKey(), envVariable.getValue()));
    }

    public static void assertContainerNoEnvVariablesConfigured(@NonNull GenericContainer<?> container) {
        verify(container, never()).withEnv(anyString(), anyString());
    }

    public static void assertContainerCommandExecuted(@NonNull GenericContainer<?> container,
                                                      @NonNull String command) {
        var subCommands = command.split(COMMAND_SPLITTER);

        try {
            verify(container).execInContainer(subCommands);
        } catch (IOException | InterruptedException ex) {
            throw new AssertionError("Should not happen", ex);
        }
    }

}
