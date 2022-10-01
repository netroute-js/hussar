package pl.netroute.hussar.core;

import java.util.Objects;

class PropertiesHelper {

    private PropertiesHelper() {
    }

    static void setProperty(String key, String value) {
        Objects.requireNonNull(key, "key is required");
        Objects.requireNonNull(value, "value is required");

        System.setProperty(key, value);
    }

    static void clearProperty(String key) {
        Objects.requireNonNull(key, "key is required");

        System.clearProperty(key);
    }

}
