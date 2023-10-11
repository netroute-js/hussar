package pl.netroute.hussar.core.domain;

import pl.netroute.hussar.core.Endpoint;
import pl.netroute.hussar.core.api.Application;
import pl.netroute.hussar.core.api.ApplicationStartupContext;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public class TestApplication implements Application {
    private final Path configurationFile;

    public TestApplication() {
        this(null);
    }

    public TestApplication(Path configurationFile) {
        this.configurationFile = configurationFile;
    }

    @Override
    public List<Endpoint> getEndpoints() {
        return List.of();
    }

    @Override
    public Optional<Path> getConfigurationFile() {
        return Optional.ofNullable(configurationFile);
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
