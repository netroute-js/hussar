package pl.netroute.hussar.core.service.api;

import pl.netroute.hussar.core.api.Accessible;
import pl.netroute.hussar.core.api.Endpoint;
import pl.netroute.hussar.core.api.Startable;
import pl.netroute.hussar.core.api.Stoppable;
import pl.netroute.hussar.core.configuration.api.BindableConfiguration;
import pl.netroute.hussar.core.network.api.ControllableNetwork;
import pl.netroute.hussar.core.service.ServiceStartupContext;

import java.util.List;

/**
 * Hussar interface that tags the {@link Service}. All implementations have to implement it.
 */
public interface Service extends Accessible, Startable<ServiceStartupContext>, Stoppable, BindableConfiguration, ControllableNetwork {

    /**
     * Returns name of the {@link Service}.
     *
     * @return the actual name of the {@link Service}.
     */
    String getName();

    /**
     * Returns direct endpoints of the {@link Service}. Those endpoints are not subject to any Network manipulation.
     * Even if {@link pl.netroute.hussar.core.network.api.NetworkControl} is used to manipulate Network conditions,
     * the {@link Service} is still accessible without any disruptions.
     *
     * @return the list of {@link Service} endpoints.
     */
    List<Endpoint> getDirectEndpoints();

}
