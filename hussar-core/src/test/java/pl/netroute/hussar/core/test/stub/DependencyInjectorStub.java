package pl.netroute.hussar.core.test.stub;

import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import pl.netroute.hussar.core.dependency.api.DependencyInjector;

import static org.mockito.Mockito.spy;

@SuperBuilder(builderMethodName = "newInstance", buildMethodName = "done")
public class DependencyInjectorStub implements DependencyInjector {

    @Override
    public void injectDependencies(@NonNull Object testInstance) {
    }

    public static DependencyInjectorStub defaultStub() {
        var dependencyInjector = DependencyInjectorStub.newInstance().done();

        return spy(dependencyInjector);
    }

}
