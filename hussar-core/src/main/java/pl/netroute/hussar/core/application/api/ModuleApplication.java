package pl.netroute.hussar.core.application.api;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import pl.netroute.hussar.core.api.Endpoint;

import java.util.List;

/**
 * An actual implementation of {@link Application}. It should be used when we want to test individual classes in a module.
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ModuleApplication implements Application {

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

    public static ModuleApplication newApplication() {
        return new ModuleApplication();
    }

}
