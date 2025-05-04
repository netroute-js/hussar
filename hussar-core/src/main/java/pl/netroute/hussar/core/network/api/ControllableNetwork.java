package pl.netroute.hussar.core.network.api;

/**
 * Interface for components that provide network control capabilities.
 * Implementations of this interface can have their network conditions
 * controlled and manipulated for testing purposes.
 */
public interface ControllableNetwork {
    /**
     * Returns the network control for this component.
     * 
     * @return the NetworkControl instance that can be used to manipulate network conditions
     */
    NetworkControl getNetworkControl();
}
