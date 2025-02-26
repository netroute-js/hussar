package pl.netroute.hussar.core.configuration.api;

import pl.netroute.hussar.core.service.api.Service;

/**
 * Hussar interface that exposes the methods to access dynamic configuration of components like {@link Service}.
 */
public interface BindableConfiguration {

    /**
     * Returns {@link ConfigurationRegistry} managed by component like {@link Service}.
     *
     * @return the actual {@link ConfigurationRegistry}
     */
    ConfigurationRegistry getConfigurationRegistry();

}
