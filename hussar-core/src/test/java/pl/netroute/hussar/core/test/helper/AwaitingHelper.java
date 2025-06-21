package pl.netroute.hussar.core.test.helper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.testcontainers.shaded.org.awaitility.Awaitility;
import org.testcontainers.shaded.org.awaitility.core.ThrowingRunnable;

import java.time.Duration;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AwaitingHelper {
    private static final Duration DEFAULT_WAIT_FOR = Duration.ofSeconds(10L);

    public static void eventually(@NonNull ThrowingRunnable assertion) {
        Awaitility
                .await()
                .atMost(DEFAULT_WAIT_FOR)
                .untilAsserted(assertion);
    }

    public static void waitFor(@NonNull Duration waitFor) {
        Awaitility
                .await()
                .pollDelay(waitFor)
                .until(() -> true);
    }

}
