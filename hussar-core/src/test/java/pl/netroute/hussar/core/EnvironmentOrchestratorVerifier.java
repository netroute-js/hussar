package pl.netroute.hussar.core;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import pl.netroute.hussar.core.environment.api.Environment;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
class EnvironmentOrchestratorVerifier {

    void verifyEnvironmentInitialized(@NonNull Environment environment) {
        verify(environment).start(any());
    }

}
