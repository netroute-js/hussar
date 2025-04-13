package pl.netroute.hussar.core.test;

import pl.netroute.hussar.core.environment.api.Environment;
import pl.netroute.hussar.core.environment.api.EnvironmentConfigurer;
import pl.netroute.hussar.core.environment.api.EnvironmentConfigurerProvider;
import pl.netroute.hussar.core.test.stub.EnvironmentConfigurerStub;
import pl.netroute.hussar.core.test.stub.EnvironmentStub;

public class TestEnvironmentConfigurerProvider implements EnvironmentConfigurerProvider {
    private final Environment environment;

    public TestEnvironmentConfigurerProvider() {
        this.environment = EnvironmentStub.defaultStub();
    }

    @Override
    public EnvironmentConfigurer provide() {
        return EnvironmentConfigurerStub.newStub(environment);
    }

}
