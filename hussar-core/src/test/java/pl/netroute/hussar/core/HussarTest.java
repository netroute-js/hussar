package pl.netroute.hussar.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.netroute.hussar.core.annotation.HussarEnvironment;
import pl.netroute.hussar.core.api.EnvironmentConfigurerProvider;
import pl.netroute.hussar.core.api.LocalEnvironmentConfigurer;
import pl.netroute.hussar.core.domain.ServiceTestA;
import pl.netroute.hussar.core.domain.ServiceTestB;
import pl.netroute.hussar.core.domain.TestApplication;

import java.util.concurrent.Executors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class HussarTest {
    private EnvironmentConfigurerProviderResolver configurerProviderResolver;
    private EnvironmentOrchestrator environmentOrchestrator;
    private Hussar hussar;

    @BeforeEach
    public void setup() {
        configurerProviderResolver = new EnvironmentConfigurerProviderResolver();

        var serviceStarter = new ServiceStarter(Executors.newSingleThreadExecutor());
        var servicesStopper = new ServiceStopper(Executors.newSingleThreadExecutor());

        environmentOrchestrator = spy(new EnvironmentOrchestrator(serviceStarter, servicesStopper));

        hussar = new Hussar(configurerProviderResolver, environmentOrchestrator);
    }

    @Test
    public void shouldInitializeEnvironment() {
        // given
        var testInstance = new ConfigurerAwareTest();

        // when
        hussar.initializeFor(testInstance);

        // then
        assertEnvironmentInitialized();
    }

    @Test
    public void shouldSkipInitializingEnvironmentWhenTestObjectIsNotHussarAware() {
        // given
        var testInstance = new NoConfigurerAwareTest();

        // when
        hussar.initializeFor(testInstance);

        // then
        assertEnvironmentInitializationSkipped();
    }

    @Test
    public void shouldShutdownEnvironments() {
        // given
        // when
        hussar.shutdown();

        // then
        assertEnvironmentsShutdown();
    }

    private void assertEnvironmentInitialized() {
        verify(environmentOrchestrator).initialize(isA(TestEnvironmentConfigurerProvider.class));
    }

    private void assertEnvironmentInitializationSkipped() {
        verify(environmentOrchestrator, never()).initialize(any());
    }

    private void assertEnvironmentsShutdown() {
        verify(environmentOrchestrator).shutdown();
    }

    static class NoConfigurerAwareTest {
    }

    @HussarEnvironment(configurerProvider = TestEnvironmentConfigurerProvider.class)
    static class ConfigurerAwareTest {
    }

    static class TestEnvironmentConfigurerProvider implements EnvironmentConfigurerProvider {
        private static final String PROPERTY_1 = "some.property";
        private static final String PROPERTY_VALUE_1 = "some_property_value";

        private static final String ENV_VARIABLE_1 = "SOME_ENV_VARIABLE";
        private static final String ENV_VARIABLE_VALUE_1 = "some_env_variable_value";

        public TestEnvironmentConfigurerProvider() {
        }

        @Override
        public LocalEnvironmentConfigurer provide() {
            var application = new TestApplication();
            var standaloneServiceA = new ServiceTestA();
            var standaloneServiceB = new ServiceTestB();

            return LocalEnvironmentConfigurer
                    .newInstance()
                    .withProperty(PROPERTY_1, PROPERTY_VALUE_1)
                    .withEnvironmentVariable(ENV_VARIABLE_1, ENV_VARIABLE_VALUE_1)
                    .withApplication(application)
                    .withService(standaloneServiceA)
                    .withService(standaloneServiceB)
                    .done();
        }

    }
}
