package pl.netroute.hussar.core.application.api;

import lombok.NonNull;
import pl.netroute.hussar.core.configuration.api.ConfigurationEntry;

import java.util.Set;

/**
 * Hussar context used during {@link Application} startup.
 */
public record ApplicationStartupContext(@NonNull Set<ConfigurationEntry> externalConfigurations) {
}
