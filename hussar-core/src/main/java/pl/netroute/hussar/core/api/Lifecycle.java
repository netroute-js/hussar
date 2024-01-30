package pl.netroute.hussar.core.api;

import lombok.NonNull;

public interface Lifecycle<T> {
    void start(@NonNull T context);
    void shutdown();
}
