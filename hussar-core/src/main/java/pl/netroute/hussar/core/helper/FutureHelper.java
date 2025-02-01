package pl.netroute.hussar.core.helper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import pl.netroute.hussar.core.api.InternalUseOnly;

import java.time.Duration;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * A {@link Future} helper.
 */
@InternalUseOnly
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FutureHelper {

    /**
     * It waits until a given {@link Future} finishes or timeouts.
     *
     * @param task - a task to finish or timeout.
     * @param timeout - the timeout of a given task.
     */
    public static void waitForTaskCompletion(@NonNull Future<?> task,
                                             @NonNull Duration timeout) {
        try {
            task.get(timeout.toMillis(), TimeUnit.MILLISECONDS);
        } catch(Exception ex) {
            throw new IllegalStateException("An error was thrown during completing background task process", ex);
        }
    }

}
