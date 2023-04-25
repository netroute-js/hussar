package pl.netroute.hussar.core.api;

public interface Lifecycle<T> {
    void start(T context);
    void shutdown();
}
