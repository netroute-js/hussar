package pl.netroute.hussar.core.domain;

import pl.netroute.hussar.core.Endpoint;
import pl.netroute.hussar.core.api.Service;

import java.util.List;

abstract class AbstractTestService implements Service {
    private final String name;

    public AbstractTestService(String name) {
        this.name = name;
    }

    @Override
    public List<Endpoint> getEndpoints() {
        return List.of();
    }

    @Override
    public void start() {
    }

    @Override
    public void shutdown() {
    }

    @Override
    public String getName() {
        return name;
    }

}
