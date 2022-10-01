package pl.netroute.hussar.core;

import pl.netroute.hussar.core.api.Application;
import pl.netroute.hussar.core.api.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class EnvironmentConfigurer {
    private Application application;

    private final Map<String, String> properties;
    private final List<Service> standaloneServices;

    private EnvironmentConfigurer() {
        this.properties = new HashMap<>();
        this.standaloneServices = new ArrayList<>();
    }

    public EnvironmentConfigurer withProperty(String key,
                                              String value) {
        Objects.requireNonNull(key, "key is required");
        Objects.requireNonNull(value, "value is required");

        this.properties.put(key, value);

        return this;
    }

    public EnvironmentConfigurer withStandaloneService(Service service) {
        Objects.requireNonNull(service, "service is required");

        standaloneServices.add(service);

        return this;
    }

    public EnvironmentConfigurer withApplication(Application application) {
        Objects.requireNonNull(application, "application is required");

        this.application = application;

        return this;
    }

    Environment configure() {
        Objects.requireNonNull(application, "application needs to be configured");

        var propertiesConfig = new PropertiesConfiguration(properties);
        var mocksConfig = new ServicesConfiguration(standaloneServices);

        return new Environment(
                application,
                propertiesConfig,
                mocksConfig
        );
    }

    public static EnvironmentConfigurer newConfigurer() {
        return new EnvironmentConfigurer();
    }

}
