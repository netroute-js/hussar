package pl.netroute.hussar.core.api;

import lombok.NonNull;

public record DefaultEnvironment(@NonNull Application application,
                                 @NonNull ConfigurationRegistry configurationRegistry,
                                 @NonNull ServiceRegistry serviceRegistry) implements Environment {
}
