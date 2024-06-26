package pl.netroute.hussar.core.domain;

import lombok.NoArgsConstructor;
import pl.netroute.hussar.core.api.Application;
import pl.netroute.hussar.core.api.ApplicationStartupContext;
import pl.netroute.hussar.core.api.Endpoint;

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
    public void start(ApplicationStartupContext context) {
    }

    @Override
    public void shutdown() {
    }

}
