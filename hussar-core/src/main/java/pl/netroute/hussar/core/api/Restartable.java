package pl.netroute.hussar.core.api;

import pl.netroute.hussar.core.application.api.Application;

/**
 * Hussar interface responsible for exposing methods to restart component.
 */
public interface Restartable {

    /**
     * Restarts {@link Application}.
     */
    void restart();
}
