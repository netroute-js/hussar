package pl.netroute.hussar.core.api.environment;

import pl.netroute.hussar.core.api.application.Application;
import pl.netroute.hussar.core.api.configuration.ConfigurationRegistry;
import pl.netroute.hussar.core.api.service.Service;
import pl.netroute.hussar.core.api.service.ServiceRegistry;

/**
 * Hussar interface responsible for gathering all the details of a testing {@link Environment}.
 */
public interface Environment {

    /**
     * Returns {@link Application} under the test.
     *
     * @return the instance of the {@link Application}.
     */
    Application application();

    /**
     * Returns {@link ConfigurationRegistry} which contains overridden configurations like properties/environment variables for the testing purposes.
     *
     * @return the instance of {@link ConfigurationRegistry}.
     */
    ConfigurationRegistry configurationRegistry();

    /**
     * Returns {@link ServiceRegistry} which contains all the configured {@link Service} for the testing purposes.
     *
     * @return the instance of {@link ServiceRegistry}.
     */
    ServiceRegistry serviceRegistry();

}
