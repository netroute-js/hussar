package pl.netroute.hussar.core.environment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.netroute.hussar.core.network.api.NetworkOperator;
import pl.netroute.hussar.core.test.factory.ConfigurationRegistryTestFactory;
import pl.netroute.hussar.core.test.factory.ServiceRegistryTestFactory;
import pl.netroute.hussar.core.test.stub.ApplicationStub;
import pl.netroute.hussar.core.stub.helper.StubHelper;

public class LocalEnvironmentTest {
    private LocalEnvironment environment;
    private LocalEnvironmentVerifier verifier;

    @BeforeEach
    public void setup() {
        var application = ApplicationStub.defaultStub();
        var networkOperator = StubHelper.defaultStub(NetworkOperator.class);

        var configRegistry = ConfigurationRegistryTestFactory.create();
        var serviceRegistry = ServiceRegistryTestFactory.create();

        environment = new LocalEnvironment(
                application,
                configRegistry,
                serviceRegistry,
                networkOperator
        );

        verifier = new LocalEnvironmentVerifier(networkOperator);
    }

    @Test
    public void shouldStartEnvironment() {
        // given
        // when
        environment.start(EnvironmentStartupContext.defaultContext());

        // then
        verifier.verifyEnvironmentStarted(environment);
    }

    @Test
    public void shouldShutdownEnvironment() {
        // given
        // when
        environment.shutdown();

        // then
        verifier.verifyEnvironmentShutdown(environment);
    }

}
