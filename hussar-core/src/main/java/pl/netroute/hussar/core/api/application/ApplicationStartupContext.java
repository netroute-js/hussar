package pl.netroute.hussar.core.api.application;

import lombok.NonNull;
import pl.netroute.hussar.core.api.configuration.ConfigurationEntry;

import java.util.Set;

/**
 * Hussar context used during {@link Application} startup.
 */
public record ApplicationStartupContext(@NonNull Set<ConfigurationEntry> externalConfigurations) {
}
