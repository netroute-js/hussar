package pl.netroute.hussar.core.configuration.api;

import lombok.NonNull;

/**
 * Actual implementation of {@link ConfigurationEntry}. It represents the property.
 */
public record PropertyConfigurationEntry(@NonNull String name,
                                         @NonNull String value) implements ConfigurationEntry {

    @Override
    public String formattedName() {
        return name;
    }

}
