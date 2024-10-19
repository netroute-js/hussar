package pl.netroute.hussar.core.api.environment;

import lombok.NonNull;
import pl.netroute.hussar.core.api.application.Application;
import pl.netroute.hussar.core.api.configuration.ConfigurationRegistry;
import pl.netroute.hussar.core.api.service.ServiceRegistry;

/**
 * An actual implementation of {@link Environment}.
 */
public record DefaultEnvironment(@NonNull Application application,
                                 @NonNull ConfigurationRegistry configurationRegistry,
                                 @NonNull ServiceRegistry serviceRegistry) implements Environment {
}
