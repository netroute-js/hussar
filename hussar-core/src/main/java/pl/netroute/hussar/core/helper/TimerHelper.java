package pl.netroute.hussar.core.helper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import pl.netroute.hussar.core.api.InternalUseOnly;

import java.time.Duration;

@InternalUseOnly
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TimerHelper {

    public static Duration measure(@NonNull Runnable action) {
        var start = System.nanoTime();

        action.run();

        var end = System.nanoTime();

        return Duration.ofNanos(end - start);
    }

}
