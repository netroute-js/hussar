package pl.netroute.hussar.core;

import java.util.Map;
import java.util.Objects;

class PropertiesConfigurer {

    void configure(PropertiesConfiguration properties) {
        Objects.requireNonNull(properties, "properties is required");

        setStaticProperties(properties.getProperties());
    }

    private void setStaticProperties(Map<String, String> staticProperties) {
        staticProperties
                .entrySet()
                .forEach(property -> PropertiesHelper.setProperty(property.getKey(), property.getValue()));
    }

}
