package pl.netroute.hussar.core.api.service;

import pl.netroute.hussar.core.api.Accessible;
import pl.netroute.hussar.core.api.Lifecycle;
import pl.netroute.hussar.core.api.configuration.ResolvableConfiguration;

/**
 * Hussar interface that tags the {@link Service}. All implementations have to implement it.
 */
public interface Service extends Accessible, Lifecycle<ServiceStartupContext>, ResolvableConfiguration {

    /**
     * Returns name of the {@link Service}.
     *
     * @return the actual name of the {@link Service}.
     */
    String getName();

}
