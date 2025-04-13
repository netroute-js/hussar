package pl.netroute.hussar.core.network.api;

import pl.netroute.hussar.core.api.Startable;
import pl.netroute.hussar.core.api.Stoppable;
import pl.netroute.hussar.core.network.NetworkOperatorStartupContext;

public interface NetworkOperator extends Startable<NetworkOperatorStartupContext>, Stoppable {
    NetworkConfigurer getNetworkConfigurer();
}
