package pl.netroute.hussar.core.environment.api;

import lombok.NonNull;
import pl.netroute.hussar.core.application.api.Application;
import pl.netroute.hussar.core.configuration.api.ConfigurationRegistry;
import pl.netroute.hussar.core.service.api.ServiceRegistry;

/**
 * An actual implementation of {@link Environment}.
 */
public record DefaultEnvironment(@NonNull Application application,
                                 @NonNull ConfigurationRegistry configurationRegistry,
                                 @NonNull ServiceRegistry serviceRegistry) implements Environment {
}
