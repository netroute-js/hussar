package pl.netroute.hussar.core;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

class FutureHelper {

    private FutureHelper() {
    }

    static void waitForTaskCompletion(Future<?> task,
                                      Duration timeout) {
        Objects.requireNonNull(task, "task is required");
        Objects.requireNonNull(timeout, "timeout is required");

        try {
            task.get(timeout.toMillis(), TimeUnit.MILLISECONDS);
        } catch(Exception ex) {
            throw new IllegalStateException("An error was thrown during completing background task process", ex);
        }
    }

}
