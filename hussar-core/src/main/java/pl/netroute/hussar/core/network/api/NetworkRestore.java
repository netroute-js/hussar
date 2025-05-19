package pl.netroute.hussar.core.network.api;

/**
 * Interface for restoring network conditions to their default state.
 * Typically used in testing environments to clean up after tests that
 * modify network behavior.
 */
public interface NetworkRestore {
    /**
     * Restores network conditions to their default state.
     * This method should be called to reset any network modifications
     * that were applied during testing.
     */
    void restoreToDefault();
}
