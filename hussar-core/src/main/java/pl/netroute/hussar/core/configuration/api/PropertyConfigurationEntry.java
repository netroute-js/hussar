package pl.netroute.hussar.core.configuration.api;

import lombok.NonNull;

import java.util.Objects;

/**
 * Actual implementation of {@link ConfigurationEntry}. It represents the property.
 */
public record PropertyConfigurationEntry(@NonNull String name,
                                         @NonNull String value) implements ConfigurationEntry {

    @Override
    public String formattedName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PropertyConfigurationEntry that = (PropertyConfigurationEntry) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

}
