package pl.netroute.hussar.core;

import java.util.Objects;

class ReflectionHelper {

    private ReflectionHelper() {
    }

    static <T> T newInstance(Class<T> type) {
        Objects.requireNonNull(type, "type is required");

        try {
            return type.getConstructor().newInstance();
        } catch (Exception ex) {
            throw new IllegalStateException("Could not create new instance of " + type, ex);
        }
    }

}
