package pl.netroute.hussar.core.docker;

import lombok.NonNull;
import org.testcontainers.containers.Container;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.WaitStrategyTarget;
import pl.netroute.hussar.core.api.InternalUseOnly;

@InternalUseOnly
public class DockerCommandLineRunner {
    private static final int SUCCESSFUL_COMMAND_CODE = 0;

    private static final String COMMAND_SPLITTER = " ";

    public void run(@NonNull String command,
                    @NonNull GenericContainer<?> container) {
        var result = runAndReturn(command, container);
        var resultCode = result.getExitCode();

        if(resultCode != SUCCESSFUL_COMMAND_CODE) {
            throw new IllegalStateException("Docker command has failed with [%d] code".formatted(resultCode));
        }
    }

    public Container.ExecResult runAndReturn(@NonNull String command,
                                             @NonNull GenericContainer<?> container) {
        try {
            var formattedCommand = command.split(COMMAND_SPLITTER);

            return container.execInContainer(formattedCommand);
        } catch (Exception ex) {
            throw new IllegalStateException("Could not execute Docker command", ex);
        }
    }

    public Container.ExecResult runAndReturn(@NonNull String command,
                                                    @NonNull WaitStrategyTarget waitTarget) {
        try {
            var formattedCommand = command.split(COMMAND_SPLITTER);

            return waitTarget.execInContainer(formattedCommand);
        } catch (Exception ex) {
            throw new IllegalStateException("Could not execute Docker command", ex);
        }
    }

}
