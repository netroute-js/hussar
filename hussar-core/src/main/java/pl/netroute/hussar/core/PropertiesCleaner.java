package pl.netroute.hussar.core;

import java.util.Map;
import java.util.Objects;

class PropertiesCleaner {

    void clean(PropertiesConfiguration properties) {
        Objects.requireNonNull(properties, "properties is required");

        cleanStaticProperties(properties.getProperties());
    }

    private void cleanStaticProperties(Map<String, String> staticProperties) {
        staticProperties
                .entrySet()
                .stream()
                .map(Map.Entry::getKey)
                .forEach(PropertiesHelper::clearProperty);
    }

}
