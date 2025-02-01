package pl.netroute.hussar.core.application;

import lombok.NonNull;
import pl.netroute.hussar.core.api.InternalUseOnly;
import pl.netroute.hussar.core.application.api.Application;
import pl.netroute.hussar.core.configuration.api.ConfigurationEntry;

import java.util.Set;

/**
 * Hussar context used during {@link Application} startup.
 */
@InternalUseOnly
public record ApplicationStartupContext(@NonNull Set<ConfigurationEntry> externalConfigurations) {
}
