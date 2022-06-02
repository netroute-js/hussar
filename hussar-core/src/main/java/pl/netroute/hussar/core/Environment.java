package pl.netroute.hussar.core;

import pl.netroute.hussar.core.api.Application;

import java.util.Objects;

class Environment {
    private final Application application;
    private final PropertiesConfiguration propertiesConfiguration;
    private final MocksConfiguration mocksConfiguration;

    Environment(Application application,
                PropertiesConfiguration propertiesConfiguration,
                MocksConfiguration mocksConfiguration) {
        Objects.requireNonNull(application, "applicationProvider is required");
        Objects.requireNonNull(propertiesConfiguration, "propertiesConfiguration is required");
        Objects.requireNonNull(application, "mocksConfiguration is required");

        this.application = application;
        this.propertiesConfiguration = propertiesConfiguration;
        this.mocksConfiguration = mocksConfiguration;
    }

    Application getApplicationProvider() {
        return application;
    }

    MocksConfiguration getMocksConfiguration() {
        return mocksConfiguration;
    }

    PropertiesConfiguration getPropertiesConfiguration() {
        return propertiesConfiguration;
    }

}
