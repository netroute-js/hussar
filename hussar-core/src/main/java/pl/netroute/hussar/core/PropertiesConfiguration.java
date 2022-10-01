package pl.netroute.hussar.core;

import java.util.Map;

class PropertiesConfiguration {
    private final Map<String, String> properties;

    PropertiesConfiguration(Map<String, String> properties) {
        this.properties = properties;
    }

    Map<String, String> getProperties() {
        return properties;
    }

}
