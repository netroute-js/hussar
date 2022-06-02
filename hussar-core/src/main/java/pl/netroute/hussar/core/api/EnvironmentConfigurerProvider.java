package pl.netroute.hussar.core.api;

import pl.netroute.hussar.core.EnvironmentConfigurer;

public interface EnvironmentConfigurerProvider {
    EnvironmentConfigurer provide();
}
