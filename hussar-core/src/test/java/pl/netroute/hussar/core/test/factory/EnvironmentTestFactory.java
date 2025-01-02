package pl.netroute.hussar.core.test.factory;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import pl.netroute.hussar.core.application.api.Application;
import pl.netroute.hussar.core.application.api.ModuleApplication;
import pl.netroute.hussar.core.configuration.api.DefaultConfigurationRegistry;
import pl.netroute.hussar.core.environment.api.DefaultEnvironment;
import pl.netroute.hussar.core.environment.api.Environment;
import pl.netroute.hussar.core.service.api.DefaultServiceRegistry;
import pl.netroute.hussar.core.service.api.Service;

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
