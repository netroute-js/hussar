package pl.netroute.hussar.core.network.api;

import lombok.NonNull;

import java.time.Duration;

/**
 * Interface for controlling network conditions in a test environment.
 * Provides methods to enable/disable network connectivity and simulate
 * various network conditions like bandwidth limitations and latency.
 */
public interface NetworkControl {
    /**
     * Enables network connectivity.
     * Allows network traffic to flow normally.
     */
    void enable();

    /**
     * Disables network connectivity.
     * Blocks all network traffic, simulating a network outage.
     */
    void disable();

    /**
     * Adds artificial delay to network operations, simulating network latency.
     *
     * @param delay the duration of delay to add to network operations
     */
    void delay(@NonNull Duration delay);

    /**
     * Resets network conditions to default behavior.
     *
     */
    void reset();
}
