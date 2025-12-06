package pl.netroute.hussar.core;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import pl.netroute.hussar.core.environment.api.Environment;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
class EnvironmentOrchestratorVerifier {
    private static final int SINGLE_ENVIRONMENT = 1;

    void verifyEnvironmentInitialized(@NonNull Environment environment) {
        verify(environment).start(any());
    }

    void verifyEnvironmentInitializedOnce(@NonNull List<CompletableFuture<Environment>> environmentInitializationFutures) {
        var environments = environmentInitializationFutures
                .stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toUnmodifiableSet());

        assertThat(environments)
                .hasSize(SINGLE_ENVIRONMENT)
                .allSatisfy(this::verifyEnvironmentInitialized);
    }

    void verifyEnvironmentShutdown(@NonNull Environment environment) {
        verify(environment).shutdown();
    }

}
