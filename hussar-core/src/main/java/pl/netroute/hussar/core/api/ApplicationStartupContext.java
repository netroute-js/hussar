package pl.netroute.hussar.core.api;

import java.util.Map;
import java.util.Objects;

public class ApplicationStartupContext {
    private final Map<String, Object> properties;

    public ApplicationStartupContext(Map<String, Object> properties) {
        Objects.requireNonNull(properties, "properties are required");

        this.properties = properties;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApplicationStartupContext that = (ApplicationStartupContext) o;
        return properties.equals(that.properties);
    }

    @Override
    public int hashCode() {
        return Objects.hash(properties);
    }

}
