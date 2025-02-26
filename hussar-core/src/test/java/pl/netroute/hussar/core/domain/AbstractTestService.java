package pl.netroute.hussar.core.domain;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import pl.netroute.hussar.core.api.Endpoint;
import pl.netroute.hussar.core.configuration.api.ConfigurationRegistry;
import pl.netroute.hussar.core.configuration.api.DefaultConfigurationRegistry;
import pl.netroute.hussar.core.service.api.Service;
import pl.netroute.hussar.core.service.ServiceStartupContext;

import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
abstract class AbstractTestService implements Service {
    private final String name;

    @Override
    public List<Endpoint> getEndpoints() {
        return List.of();
    }

    @Override
    public void start(@NonNull ServiceStartupContext context) {
    }

    @Override
    public void shutdown() {
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ConfigurationRegistry getConfigurationRegistry() {
        return new DefaultConfigurationRegistry();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractTestService that = (AbstractTestService) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

}
