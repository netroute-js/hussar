package pl.netroute.hussar.core.domain;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import pl.netroute.hussar.core.application.api.Application;
import pl.netroute.hussar.core.application.ApplicationStartupContext;
import pl.netroute.hussar.core.api.Endpoint;
import pl.netroute.hussar.core.dependency.NoOpDependencyInjector;
import pl.netroute.hussar.core.dependency.api.DependencyInjector;

import java.util.List;

@NoArgsConstructor
public class TestApplication implements Application {

    @Override
    public List<Endpoint> getEndpoints() {
        return List.of();
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
        return NoOpDependencyInjector.newInstance();
    }

}
