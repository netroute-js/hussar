package pl.netroute.hussar.core.application.api;

import lombok.NonNull;
import pl.netroute.hussar.core.api.Endpoint;
import pl.netroute.hussar.core.application.ApplicationStartupContext;
import pl.netroute.hussar.core.dependency.NoOpDependencyInjector;
import pl.netroute.hussar.core.dependency.api.DependencyInjector;

import java.util.List;

/**
 * An actual implementation of {@link Application}. It should be used when we want to test individual classes in a module.
 */
public class ModuleApplication implements Application {
    private final List<Endpoint> endpoints;
    private final DependencyInjector dependencyInjector;

    private ModuleApplication() {
        this.endpoints = List.of();
        this.dependencyInjector = NoOpDependencyInjector.newInstance();
    }

    @Override
    public List<Endpoint> getEndpoints() {
        return endpoints;
    }

    @Override
    public boolean isInitialized() {
        return true;
    }

    @Override
    public void start(@NonNull ApplicationStartupContext context) {
    }

    @Override
    public void restart() {
    }

    @Override
    public void shutdown() {
    }

    @Override
    public DependencyInjector getDependencyInjector() {
        return dependencyInjector;
    }

    public static ModuleApplication newApplication() {
        return new ModuleApplication();
    }

}
