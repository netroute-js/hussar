package pl.netroute.hussar.core.network.api;

import pl.netroute.hussar.core.api.Accessible;

/**
 * Interface representing a network component in the system.
 * Combines the capabilities of being accessible through endpoints
 * and having controllable network conditions for testing purposes.
 * <p>
 * Implementations provide access to their endpoints and allow
 * manipulation of network conditions.
 */
public interface Network extends Accessible, ControllableNetwork {
}
