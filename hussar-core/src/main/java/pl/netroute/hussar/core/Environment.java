package pl.netroute.hussar.core;

import pl.netroute.hussar.core.api.Application;
import pl.netroute.hussar.core.api.ConfigurationRegistry;
import pl.netroute.hussar.core.api.ServiceRegistry;

import java.util.Objects;

class Environment {
    private final Application application;
    private final ConfigurationRegistry staticConfigurationRegistry;
    private final ServiceRegistry serviceRegistry;

    Environment(Application application,
                ConfigurationRegistry staticConfigurationRegistry,
                ServiceRegistry serviceRegistry) {
        Objects.requireNonNull(application, "applicationProvider is required");
        Objects.requireNonNull(staticConfigurationRegistry, "staticConfigurationRegistry is required");
        Objects.requireNonNull(serviceRegistry, "serviceRegistry is required");

        this.application = application;
        this.staticConfigurationRegistry = staticConfigurationRegistry;
        this.serviceRegistry = serviceRegistry;
    }

    Application getApplication() {
        return application;
    }

    ServiceRegistry getServiceRegistry() {
        return serviceRegistry;
    }

    ConfigurationRegistry getStaticConfigurationRegistry() {
        return staticConfigurationRegistry;
    }

}
