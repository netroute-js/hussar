package pl.netroute.hussar.core.api;

import lombok.NonNull;

/**
 * Hussar interface responsible for exposing lifecycle methods of {@link Application} or {@link Service}.
 *
 * @param <T> is used to parametrize the lifecycle context.
 */
public interface Lifecycle<T> {

    /**
     * Starts {@link Application} or {@link Service}.
     *
     * @param context - used to pass additional data during startup of {@link Application} or {@link Service}.
     */
    void start(@NonNull T context);

    /**
     * Shutdowns {@link Application} or {@link Service}.
     */
    void shutdown();

}
