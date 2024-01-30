package pl.netroute.hussar.core.api;

import lombok.NonNull;

import java.util.Map;

public record ApplicationStartupContext(@NonNull Map<String, Object> properties) {
}
