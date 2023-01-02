package pl.netroute.hussar.core.helper;

import java.util.Objects;
import java.util.Optional;

public class PropertiesHelper {

    private PropertiesHelper() {
    }

    public static void setProperty(String key, String value) {
        Objects.requireNonNull(key, "key is required");
        Objects.requireNonNull(value, "value is required");

        System.setProperty(key, value);
    }

    public static Optional<String> getProperty(String key) {
        Objects.requireNonNull(key, "key is required");

        return Optional.ofNullable(System.getProperty(key));
    }

    public static Optional<Integer> getIntProperty(String key) {
        Objects.requireNonNull(key, "key is required");

        return Optional
                .ofNullable(System.getProperty(key))
                .filter(property -> !property.isBlank())
                .map(Integer::parseInt);
    }

    public static void clearProperty(String key) {
        Objects.requireNonNull(key, "key is required");

        System.clearProperty(key);
    }

}
