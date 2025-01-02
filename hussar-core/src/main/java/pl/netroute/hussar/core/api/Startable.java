package pl.netroute.hussar.core.api;

import lombok.NonNull;

/**
 * Hussar interface responsible for exposing methods to start component.
 *
 * @param <T> is used to parametrize the context type.
 */
public interface Startable<T> {

    /**
     * Starts component.
     *
     * @param context - used to pass additional data during startup of a component.
     */
    void start(@NonNull T context);

}
