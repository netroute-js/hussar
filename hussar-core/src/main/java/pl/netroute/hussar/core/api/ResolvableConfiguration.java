package pl.netroute.hussar.core.api;

/**
 * Hussar interface that exposes the methods to access dynamic configuration of components like {@link Service}.
 */
public interface ResolvableConfiguration {

    /**
     * Returns {@link ConfigurationRegistry} managed by component like {@link Service}.
     *
     * @return the actual {@link ConfigurationRegistry}
     */
    ConfigurationRegistry getConfigurationRegistry();

}
