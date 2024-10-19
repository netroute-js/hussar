package pl.netroute.hussar.core.api.environment;

/**
 * Hussar interface responsible for providing {@link EnvironmentConfigurer}.
 */
public interface EnvironmentConfigurerProvider {

    /**
     * Provides {@link EnvironmentConfigurer} so that Hussar can maintain/orchestrate the lifecycle of the testing environment.
     *
     * @return the instance of {@link EnvironmentConfigurer}
     */
    EnvironmentConfigurer provide();

}
