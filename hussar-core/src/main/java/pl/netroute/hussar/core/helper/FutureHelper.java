package pl.netroute.hussar.core.helper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.time.Duration;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FutureHelper {

    public static void waitForTaskCompletion(@NonNull Future<?> task,
                                             @NonNull Duration timeout) {
        try {
            task.get(timeout.toMillis(), TimeUnit.MILLISECONDS);
        } catch(Exception ex) {
            throw new IllegalStateException("An error was thrown during completing background task process", ex);
        }
    }

}
