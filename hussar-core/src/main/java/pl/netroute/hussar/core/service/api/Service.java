package pl.netroute.hussar.core.service.api;

import pl.netroute.hussar.core.api.Accessible;
import pl.netroute.hussar.core.api.Startable;
import pl.netroute.hussar.core.api.Stoppable;
import pl.netroute.hussar.core.configuration.api.BindableConfiguration;
import pl.netroute.hussar.core.network.api.ControllableNetwork;
import pl.netroute.hussar.core.service.ServiceStartupContext;

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

}
