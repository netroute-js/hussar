package pl.netroute.hussar.core.api;

import pl.netroute.hussar.core.api.application.Application;

/**
 * Hussar interface responsible for exposing methods to restart {@link Application}.
 */
public interface Restartable {

    /**
     * Restarts {@link Application}.
     */
    void restart();
}
