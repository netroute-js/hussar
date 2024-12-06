package pl.netroute.hussar.core.test.factory;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import pl.netroute.hussar.core.api.application.Application;
import pl.netroute.hussar.core.api.application.ModuleApplication;
import pl.netroute.hussar.core.api.configuration.DefaultConfigurationRegistry;
import pl.netroute.hussar.core.api.environment.DefaultEnvironment;
import pl.netroute.hussar.core.api.environment.Environment;
import pl.netroute.hussar.core.api.service.DefaultServiceRegistry;
import pl.netroute.hussar.core.api.service.Service;

import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EnvironmentTestFactory {

    public static Environment create() {
        var application = ModuleApplication.newApplication();
        var configurationRegistry = new DefaultConfigurationRegistry();
        var serviceRegistry = new DefaultServiceRegistry();

        return new DefaultEnvironment(application, configurationRegistry, serviceRegistry);
    }

    public static Environment create(@NonNull Application application,
                                     @NonNull Set<Service> services) {
        var configurationRegistry = new DefaultConfigurationRegistry();
        var serviceRegistry = new DefaultServiceRegistry(services);

        return new DefaultEnvironment(application, configurationRegistry, serviceRegistry);
    }

}
