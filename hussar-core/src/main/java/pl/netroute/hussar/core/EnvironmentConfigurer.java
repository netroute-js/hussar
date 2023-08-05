package pl.netroute.hussar.core;

import pl.netroute.hussar.core.api.Application;
import pl.netroute.hussar.core.api.ConfigurationEntry;
import pl.netroute.hussar.core.api.ConfigurationRegistry;
import pl.netroute.hussar.core.api.MapConfigurationRegistry;
import pl.netroute.hussar.core.api.MapServiceRegistry;
import pl.netroute.hussar.core.api.Service;
import pl.netroute.hussar.core.api.ServiceRegistry;

import java.util.Objects;

public final class EnvironmentConfigurer {
    private Application application;

    private final ConfigurationRegistry staticConfigurationRegistry;
    private final ServiceRegistry serviceRegistry;

    private EnvironmentConfigurer() {
        this.staticConfigurationRegistry = new MapConfigurationRegistry();
        this.serviceRegistry = new MapServiceRegistry();
    }

    public EnvironmentConfigurer withStaticConfigurationEntry(ConfigurationEntry configEntry) {
        Objects.requireNonNull(configEntry, "configEntry is required");

        this.staticConfigurationRegistry.register(configEntry);

        return this;
    }

    public EnvironmentConfigurer withStandaloneService(Service service) {
        Objects.requireNonNull(service, "service is required");

        serviceRegistry.register(service);

        return this;
    }

    public EnvironmentConfigurer withApplication(Application application) {
        Objects.requireNonNull(application, "application is required");

        this.application = application;

        return this;
    }

    Environment configure() {
        Objects.requireNonNull(application, "application needs to be configured");

        return new Environment(
                application,
                staticConfigurationRegistry,
                serviceRegistry
        );
    }

    public static EnvironmentConfigurer newConfigurer() {
        return new EnvironmentConfigurer();
    }

}
