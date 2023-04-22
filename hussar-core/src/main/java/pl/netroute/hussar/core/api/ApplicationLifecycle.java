package pl.netroute.hussar.core.api;

public interface ApplicationLifecycle {
    void start(ApplicationStartupContext context);
    void shutdown();
}
