package pl.netroute.hussar.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.netroute.hussar.core.annotation.HussarEnvironment;
import pl.netroute.hussar.core.api.Application;
import pl.netroute.hussar.core.api.EnvironmentConfigurerProvider;
import pl.netroute.hussar.core.api.Service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

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
        private static final String PROPERTY_1 = "property1";
        private static final String PROPERTY_VALUE_1 = "property_value1";

        private static final String PROPERTY_2 = "property2";
        private static final String PROPERTY_VALUE_2 = "property_value2";

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
                    .withProperty(PROPERTY_1, PROPERTY_VALUE_1)
                    .withProperty(PROPERTY_2, PROPERTY_VALUE_2);
        }

    }
}
