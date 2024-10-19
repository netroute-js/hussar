package pl.netroute.hussar.core.api.environment;

/**
 * Hussar interface responsible for configuring a new instance of testing {@link Environment}.
 */
public interface EnvironmentConfigurer {

    /**
     * Configures testing {@link Environment}.
     *
     * @return the instance of testing {@link Environment}.
     */
    Environment configure();

}
