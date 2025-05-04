package pl.netroute.hussar.core.network.api;

import pl.netroute.hussar.core.api.Startable;
import pl.netroute.hussar.core.api.Stoppable;
import pl.netroute.hussar.core.network.NetworkOperatorStartupContext;

/**
 * Provides functionality to start and stop the network operator,
 * as well as access to a network configurer for creating and configuring networks.
 */
public interface NetworkOperator extends Startable<NetworkOperatorStartupContext>, Stoppable {
    /**
     * Returns the network configurer associated with this operator.
     *
     * @return the NetworkConfigurer instance
     */
    NetworkConfigurer getNetworkConfigurer();
}
