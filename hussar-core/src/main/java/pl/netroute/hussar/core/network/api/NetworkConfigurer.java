package pl.netroute.hussar.core.network.api;

import lombok.NonNull;
import pl.netroute.hussar.core.api.Endpoint;

import java.util.List;

/**
 * Interface responsible for configuring network.
 */
public interface NetworkConfigurer {
    /**
     * Configures a network for the given parameters.
     *
     * @param networkPrefix a unique identifier for the network being configured
     * @param endpoints the list of endpoints to be included in the network
     * @return a configured Network instance ready for use
     */
    Network configure(@NonNull String networkPrefix, @NonNull List<Endpoint> endpoints);
}
