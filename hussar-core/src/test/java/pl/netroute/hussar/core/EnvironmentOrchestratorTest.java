package pl.netroute.hussar.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.netroute.hussar.core.test.TestEnvironmentConfigurerProvider;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

public class EnvironmentOrchestratorTest {
    private static final int SINGLE_ENVIRONMENT = 1;

    private EnvironmentOrchestrator orchestrator;
    private EnvironmentOrchestratorVerifier verifier;

    @BeforeEach
    public void setup() {
        orchestrator = new EnvironmentOrchestrator();

        verifier = new EnvironmentOrchestratorVerifier();
    }

    @Test
    public void shouldInitializeEnvironment() {
        // given
        var configurerProvider = new TestEnvironmentConfigurerProvider();

        // when
        var environment = orchestrator.initialize(configurerProvider);

        // then
        verifier.verifyEnvironmentInitialized(environment);
    }

    @Test
    public void shouldInitializeEnvironmentOnceWhenMultipleThreadsTry() {
        // given
        var threadsNumber = 10;
        var configurerProvider = new TestEnvironmentConfigurerProvider();

        // when
        var initializationFutures = IntStream
                .range(0, threadsNumber)
                .mapToObj(index -> CompletableFuture.supplyAsync(() -> orchestrator.initialize(configurerProvider)))
                .toList();

        CompletableFuture
                .allOf(initializationFutures.toArray(new CompletableFuture[0]))
                .join();

        // then
        var environments = initializationFutures
                .stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toUnmodifiableSet());

        assertThat(environments)
                .hasSize(SINGLE_ENVIRONMENT)
                .allSatisfy(verifier::verifyEnvironmentInitialized);
    }

}
