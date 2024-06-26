package pl.netroute.hussar.core.api;

import lombok.NonNull;

import java.util.Set;

public record ApplicationStartupContext(@NonNull Set<ConfigurationEntry> externalConfigurations) {
}
