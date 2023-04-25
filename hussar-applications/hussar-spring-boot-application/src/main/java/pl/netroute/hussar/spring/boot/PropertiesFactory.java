package pl.netroute.hussar.spring.boot;

import pl.netroute.hussar.core.helper.PortFinderHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

class PropertiesFactory {
    public static final String SERVER_PORT = "server.port";

    private PropertiesFactory() {}

    static Map<String, Object> createWithDynamicPort(Map<String, Object> properties) {
        Objects.requireNonNull(properties, "properties are required");

        var mutableProperties = new HashMap<>(properties);
        mutableProperties.put(SERVER_PORT, PortFinderHelper.findFreePort());

        return Map.copyOf(mutableProperties);
    }
}
