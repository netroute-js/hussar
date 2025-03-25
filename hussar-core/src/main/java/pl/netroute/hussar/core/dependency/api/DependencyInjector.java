package pl.netroute.hussar.core.dependency.api;

import lombok.NonNull;

public interface DependencyInjector {
    void injectDependencies(@NonNull Object testInstance);
}
