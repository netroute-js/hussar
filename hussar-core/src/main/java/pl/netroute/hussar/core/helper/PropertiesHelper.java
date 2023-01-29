package pl.netroute.hussar.core.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Optional;

public class PropertiesHelper {
    private static final Logger LOG = LoggerFactory.getLogger(PropertiesHelper.class);

    private PropertiesHelper() {
    }

    public static void setProperty(String key, String value) {
        Objects.requireNonNull(key, "key is required");
        Objects.requireNonNull(value, "value is required");

        LOG.info("Setting {} property to {}", key, value);

        System.setProperty(key, value);
    }

    public static Optional<String> getProperty(String key) {
        Objects.requireNonNull(key, "key is required");

        return Optional.ofNullable(System.getProperty(key));
    }

    public static String getPropertyOrFail(String key) {
        ValidatorHelper.requireNonEmpty(key, "key is required");

        return getProperty(key).orElseThrow();
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

        LOG.info("Clearing {} property", key);

        System.clearProperty(key);
    }

}
