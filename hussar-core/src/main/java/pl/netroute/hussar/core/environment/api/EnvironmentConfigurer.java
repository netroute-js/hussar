package pl.netroute.hussar.core.environment.api;

import lombok.NonNull;
import pl.netroute.hussar.core.environment.EnvironmentConfigurerContext;

/**
 * Hussar interface responsible for configuring a new instance of testing {@link Environment}.
 */
public interface EnvironmentConfigurer {

    /**
     * Configures testing {@link Environment}.
     *
     * @param context - a context for accessing Hussar's environment features.
     * @return the instance of testing {@link Environment}.
     */
    Environment configure(@NonNull EnvironmentConfigurerContext context);

}
