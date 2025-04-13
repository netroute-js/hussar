package pl.netroute.hussar.core.test.stub;

import lombok.Builder;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import pl.netroute.hussar.core.application.api.Application;
import pl.netroute.hussar.core.configuration.api.ConfigurationRegistry;
import pl.netroute.hussar.core.environment.EnvironmentStartupContext;
import pl.netroute.hussar.core.environment.api.Environment;
import pl.netroute.hussar.core.service.api.ServiceRegistry;
import pl.netroute.hussar.core.test.factory.ConfigurationRegistryTestFactory;
import pl.netroute.hussar.core.test.factory.ServiceRegistryTestFactory;

import static org.mockito.Mockito.spy;

@SuperBuilder(builderMethodName = "newInstance", buildMethodName = "done")
public class EnvironmentStub implements Environment {

    @Builder.Default
    private final Application application = ApplicationStub.defaultStub();

    @Builder.Default
    private final ConfigurationRegistry configurationRegistry = ConfigurationRegistryTestFactory.create();

    @Builder.Default
    private final ServiceRegistry serviceRegistry = ServiceRegistryTestFactory.create();

    @Override
    public Application getApplication() {
        return application;
    }

    @Override
    public ConfigurationRegistry getConfigurationRegistry() {
        return configurationRegistry;
    }

    @Override
    public ServiceRegistry getServiceRegistry() {
        return serviceRegistry;
    }

    @Override
    public void start(@NonNull EnvironmentStartupContext context) {
    }

    @Override
    public void shutdown() {
    }

    public static EnvironmentStub defaultStub() {
        var environment = EnvironmentStub.newInstance().done();

        return spy(environment);
    }

}
