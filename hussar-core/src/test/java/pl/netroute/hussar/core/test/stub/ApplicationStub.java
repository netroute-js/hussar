package pl.netroute.hussar.core.test.stub;

import lombok.Builder;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import pl.netroute.hussar.core.api.Endpoint;
import pl.netroute.hussar.core.application.ApplicationStartupContext;
import pl.netroute.hussar.core.application.api.Application;
import pl.netroute.hussar.core.dependency.api.DependencyInjector;
import pl.netroute.hussar.core.test.factory.EndpointTestFactory;

import java.util.List;

import static org.mockito.Mockito.spy;

@SuperBuilder(builderMethodName = "newInstance", buildMethodName = "done")
public class ApplicationStub implements Application {

    @Builder.Default
    private final boolean initialized = true;

    @Builder.Default
    private final List<Endpoint> endpoints = List.of(EndpointTestFactory.createHttp());

    @Builder.Default
    private final DependencyInjector dependencyInjector = DependencyInjectorStub.defaultStub();

    @Override
    public List<Endpoint> getEndpoints() {
        return endpoints;
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }

    @Override
    public void restart() {
    }

    @Override
    public void start(@NonNull ApplicationStartupContext context) {
    }

    @Override
    public void shutdown() {
    }

    @Override
    public DependencyInjector getDependencyInjector() {
        return dependencyInjector;
    }

    public static ApplicationStub defaultStub() {
        var application = ApplicationStub.newInstance().done();

        return spy(application);
    }

}
