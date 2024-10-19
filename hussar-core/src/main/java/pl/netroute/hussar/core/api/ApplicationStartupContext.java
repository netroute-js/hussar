package pl.netroute.hussar.core.api;

import lombok.NonNull;

import java.util.Set;

/**
 * Hussar context used during {@link Application} startup.
 */
public record ApplicationStartupContext(@NonNull Set<ConfigurationEntry> externalConfigurations) {
}
