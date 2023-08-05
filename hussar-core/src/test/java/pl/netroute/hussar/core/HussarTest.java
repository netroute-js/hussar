package pl.netroute.hussar.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.netroute.hussar.core.annotation.HussarEnvironment;
import pl.netroute.hussar.core.api.*;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class HussarTest {
    private EnvironmentConfigurerProviderResolver configurerProviderResolver;
    private EnvironmentOrchestrator environmentOrchestrator;
    private Hussar hussar;

    @BeforeEach
    public void setup() {
        configurerProviderResolver = new EnvironmentConfigurerProviderResolver();
        environmentOrchestrator = mock(EnvironmentOrchestrator.class);

        hussar = new Hussar(configurerProviderResolver, environmentOrchestrator);
    }

    @Test
    public void shouldInitializeEnvironment() {
        // given
        var testInstance = new ConfigurerAwareTest();

        var environment = mock(Environment.class);

        when(environment.getServiceRegistry()).thenReturn(new MapServiceRegistry());
        when(environmentOrchestrator.initialize(isA(TestEnvironmentConfigurerProvider.class))).thenReturn(environment);

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

        private final Application application = mock(Application.class);
        private final Service standaloneServiceA = mock(Service.class);
        private final Service standaloneServiceB = mock(Service.class);

        public TestEnvironmentConfigurerProvider() {
        }

        @Override
        public EnvironmentConfigurer provide() {
            return EnvironmentConfigurer
                    .newConfigurer()
                    .withApplication(application)
                    .withStandaloneService(standaloneServiceA)
                    .withStandaloneService(standaloneServiceB)
                    .withStaticConfigurationEntry(ConfigurationEntry.property(PROPERTY_1, PROPERTY_VALUE_1))
                    .withStaticConfigurationEntry(ConfigurationEntry.envVariable(ENV_VARIABLE_1, ENV_VARIABLE_VALUE_1));
        }

    }
}
