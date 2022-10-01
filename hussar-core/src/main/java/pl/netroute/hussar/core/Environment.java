package pl.netroute.hussar.core;

import pl.netroute.hussar.core.api.Application;

import java.util.Objects;

class Environment {
    private final Application application;
    private final PropertiesConfiguration propertiesConfiguration;
    private final ServicesConfiguration servicesConfiguration;

    Environment(Application application,
                PropertiesConfiguration propertiesConfiguration,
                ServicesConfiguration servicesConfiguration) {
        Objects.requireNonNull(application, "applicationProvider is required");
        Objects.requireNonNull(propertiesConfiguration, "propertiesConfiguration is required");
        Objects.requireNonNull(servicesConfiguration, "servicesConfiguration is required");

        this.application = application;
        this.propertiesConfiguration = propertiesConfiguration;
        this.servicesConfiguration = servicesConfiguration;
    }

    Application getApplication() {
        return application;
    }

    ServicesConfiguration getServicesConfiguration() {
        return servicesConfiguration;
    }

    PropertiesConfiguration getPropertiesConfiguration() {
        return propertiesConfiguration;
    }

}
