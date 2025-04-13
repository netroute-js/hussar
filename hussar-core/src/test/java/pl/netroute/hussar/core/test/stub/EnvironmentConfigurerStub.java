package pl.netroute.hussar.core.test.stub;

import lombok.Builder;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import pl.netroute.hussar.core.environment.EnvironmentConfigurerContext;
import pl.netroute.hussar.core.environment.api.Environment;
import pl.netroute.hussar.core.environment.api.EnvironmentConfigurer;

import static org.mockito.Mockito.spy;

@SuperBuilder(builderMethodName = "newInstance", buildMethodName = "done")
public class EnvironmentConfigurerStub implements EnvironmentConfigurer {

    @Builder.Default
    private final Environment environment = EnvironmentStub.defaultStub();

    @Override
    public Environment configure(@NonNull EnvironmentConfigurerContext context) {
        return environment;
    }

    public static EnvironmentConfigurerStub defaultStub() {
        var environmentConfigurer = EnvironmentConfigurerStub.newInstance().done();

        return spy(environmentConfigurer);
    }

    public static EnvironmentConfigurerStub newStub(@NonNull Environment environment) {
        return EnvironmentConfigurerStub
                .newInstance()
                .environment(environment)
                .done();
    }

}
