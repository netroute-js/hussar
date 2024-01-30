package pl.netroute.hussar.spring.boot;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import pl.netroute.hussar.core.helper.PortFinderHelper;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class PropertiesFactory {
    public static final String SERVER_PORT = "server.port";

    static Map<String, Object> createWithDynamicPort(@NonNull Map<String, Object> properties) {
        var mutableProperties = new HashMap<>(properties);
        mutableProperties.put(SERVER_PORT, PortFinderHelper.findFreePort());

        return Map.copyOf(mutableProperties);
    }
}
