package pl.netroute.hussar.core.network.api;

import lombok.NonNull;

import java.time.Duration;

public interface NetworkControl {
    void enable();
    void disable();
    void bandwidth(long kilobytesPerSecond);
    void delay(@NonNull Duration delay);
}
