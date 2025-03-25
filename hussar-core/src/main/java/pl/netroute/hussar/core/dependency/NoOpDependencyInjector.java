package pl.netroute.hussar.core.dependency;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import pl.netroute.hussar.core.dependency.api.DependencyInjector;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NoOpDependencyInjector implements DependencyInjector {

    @Override
    public void injectDependencies(@NonNull Object testInstance) {
    }

    public static NoOpDependencyInjector newInstance() {
        return new NoOpDependencyInjector();
    }

}
