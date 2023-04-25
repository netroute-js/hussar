package pl.netroute.hussar.core.api;

import java.nio.file.Path;
import java.util.Optional;

public interface Configurable {
    Optional<Path> getConfigurationFile();
}
