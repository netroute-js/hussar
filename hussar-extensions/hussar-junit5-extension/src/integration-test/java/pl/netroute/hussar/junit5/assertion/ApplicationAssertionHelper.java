package pl.netroute.hussar.junit5.assertion;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import pl.netroute.hussar.core.api.application.Application;

import static org.assertj.core.api.Assertions.assertThat;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ApplicationAssertionHelper {

    public static void assertApplicationBootstrapped(@NonNull Application application) {
        assertThat(application.isInitialized()).isTrue();
    }

}
