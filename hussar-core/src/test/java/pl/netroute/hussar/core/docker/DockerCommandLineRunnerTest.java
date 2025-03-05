package pl.netroute.hussar.core.docker;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.testcontainers.containers.Container;
import org.testcontainers.containers.GenericContainer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DockerCommandLineRunnerTest {
    private static final int SUCCESSFUL_COMMAND_CODE = 0;
    private static final int FAILED_COMMAND_CODE = -1;
    private static final String COMMAND_SPLITTER = " ";

    private DockerCommandLineRunner commandLineRunner;

    @BeforeEach
    public void setup() {
        commandLineRunner = new DockerCommandLineRunner();
    }

    @ParameterizedTest
    @ValueSource(ints = {SUCCESSFUL_COMMAND_CODE, FAILED_COMMAND_CODE})
    public void shouldRunDockerCommand() {
        // given
        var command = "docker command";
        var container = createContainer();

        givenContainerCommand(command, SUCCESSFUL_COMMAND_CODE, container);

        // when
        commandLineRunner.run(command, container);

        // then
        assertContainerCommandExecuted(command, container);
    }

    @ParameterizedTest
    @ValueSource(ints = {SUCCESSFUL_COMMAND_CODE, FAILED_COMMAND_CODE})
    public void shouldRunDockerCommandAndReturn(int exitCode) {
        // given
        var command = "docker command";
        var container = createContainer();

        givenContainerCommand(command, exitCode, container);

        // when
        var result = commandLineRunner.runAndReturn(command, container);

        // then
        assertContainerCommandResult(result, exitCode);
        assertContainerCommandExecuted(command, container);
    }

    @Test
    public void shouldFailRunningDockerCommandWhenWrongCommandCodeReturned() {
        // given
        var command = "docker command";
        var container = createContainer();

        givenContainerCommand(command, FAILED_COMMAND_CODE, container);

        // when
        // then
        assertThatThrownBy(() -> commandLineRunner.run(command, container))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Docker command has failed with [%d] code".formatted(FAILED_COMMAND_CODE));

        assertContainerCommandExecuted(command, container);
    }

    @Test
    public void shouldFailRunningDockerCommandWhenContainerExecutionFails() {
        // given
        var command = "docker command";
        var failure = new IllegalStateException("Controlled Exception");
        var container = createContainer();

        givenContainerCommandExecutionFails(container, failure);

        // when
        // then
        assertThatThrownBy(() -> commandLineRunner.run(command, container))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Could not execute Docker command")
                .hasCause(failure);

        assertContainerCommandExecuted(command, container);
    }

    @Test
    public void shouldFailRunningDockerCommandAndReturnWhenContainerExecutionFails() {
        // given
        var command = "docker command";
        var failure = new IllegalStateException("Controlled Exception");
        var container = createContainer();

        givenContainerCommandExecutionFails(container, failure);

        // when
        // then
        assertThatThrownBy(() -> commandLineRunner.runAndReturn(command, container))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Could not execute Docker command")
                .hasCause(failure);

        assertContainerCommandExecuted(command, container);
    }

    private GenericContainer<?> createContainer() {
        return mock(GenericContainer.class);
    }

    private void givenContainerCommand(String command, int exitCode, GenericContainer<?> container) {
        var preparedCommand = command.split(COMMAND_SPLITTER);

        var result = mock(Container.ExecResult.class);
        when(result.getExitCode()).thenReturn(exitCode);

        try {
            when(container.execInContainer(preparedCommand)).thenReturn(result);
        } catch (Exception ex) {
            throw new AssertionError("Should not happen", ex);
        }
    }

    private void givenContainerCommandExecutionFails(GenericContainer<?> container, Exception failure) {
        try {
            when(container.execInContainer(any(String[].class))).thenThrow(failure);
        } catch (Exception ex) {
            throw new AssertionError("Should not happen", ex);
        }
    }

    private void assertContainerCommandResult(Container.ExecResult result, int expectedExitCode) {
        assertThat(result.getExitCode()).isEqualTo(expectedExitCode);
    }

    private void assertContainerCommandExecuted(String command, GenericContainer<?> container) {
        var preparedCommand = command.split(COMMAND_SPLITTER);

        try {
            verify(container).execInContainer(preparedCommand);
        } catch (Exception ex) {
            throw new AssertionError("Should not happen", ex);
        }
    }

}
