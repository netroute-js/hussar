package pl.netroute.hussar.core.api;

/**
 * Hussar interface responsible for exposing methods to stop component.
 */
public interface Stoppable {

    /**
     * Shutdowns component.
     */
    void shutdown();

}
