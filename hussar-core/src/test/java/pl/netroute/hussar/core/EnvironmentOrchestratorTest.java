package pl.netroute.hussar.core;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.netroute.hussar.core.test.AnotherTestEnvironmentConfigurerProvider;
import pl.netroute.hussar.core.test.TestEnvironmentConfigurerProvider;

import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

class EnvironmentOrchestratorTest {
    private EnvironmentOrchestrator orchestrator;
    private EnvironmentOrchestratorVerifier verifier;

    @BeforeEach
    void setup() {
        orchestrator = new EnvironmentOrchestrator();

        verifier = new EnvironmentOrchestratorVerifier();
    }

    @Test
    void shouldInitializeEnvironment() {
        // given
        var configurerProvider = new TestEnvironmentConfigurerProvider();

        // when
        var environment = orchestrator.initialize(configurerProvider);

        // then
        verifier.verifyEnvironmentInitialized(environment);
    }

    @Test
    void shouldInitializeEnvironmentOnceWhenMultipleThreadsTry() {
        // given
        var threadsNumber = 10;
        var configurerProvider = new TestEnvironmentConfigurerProvider();

        // when
        var environmentInitializationFutures = IntStream
                .range(0, threadsNumber)
                .mapToObj(index -> CompletableFuture.supplyAsync(() -> orchestrator.initialize(configurerProvider)))
                .toList();

        CompletableFuture
                .allOf(environmentInitializationFutures.toArray(new CompletableFuture[0]))
                .join();

        // then
        verifier.verifyEnvironmentInitializedOnce(environmentInitializationFutures);
    }

    @Test
    void shouldFailInitializingWhenEnvironmentStartupFailed() {
        // given
        var configurerProvider = new TestEnvironmentConfigurerProvider();
        var environment = configurerProvider.getEnvironment();
        var initializationFailure = new IllegalStateException("Controlled Exception");

        doThrow(initializationFailure)
                .when(environment)
                .start(any());

        // when
        // then
        Assertions.assertThatThrownBy(() -> orchestrator.initialize(configurerProvider))
                .isInstanceOf(HussarException.class)
                .hasMessage("Environment initialization failed")
                .hasCause(initializationFailure);
    }

    @Test
    void shouldShutdownEnvironments() {
        // given
        var firstConfigurerProvider = new TestEnvironmentConfigurerProvider();
        var secondConfigurerProvider = new AnotherTestEnvironmentConfigurerProvider();

        // when
        var firstEnvironment = orchestrator.initialize(firstConfigurerProvider);
        var secondEnvironment = orchestrator.initialize(secondConfigurerProvider);

        orchestrator.shutdown();

        // then
        verifier.verifyEnvironmentShutdown(firstEnvironment);
        verifier.verifyEnvironmentShutdown(secondEnvironment);
    }

    @Test
    void shouldTryShuttingDownFailedEnvironment() {
        // given
        var configurerProvider = new TestEnvironmentConfigurerProvider();
        var environment = configurerProvider.getEnvironment();
        var initializationFailure = new IllegalStateException("Controlled Exception");

        doThrow(initializationFailure)
                .when(environment)
                .start(any());

        // when
        try {
            orchestrator.initialize(configurerProvider);
        } catch (Exception ex) {
            // It fails so we can simulate shutdown of failed environment
        }

        orchestrator.shutdown();

        // then
        verifier.verifyEnvironmentShutdown(environment);
    }

}
