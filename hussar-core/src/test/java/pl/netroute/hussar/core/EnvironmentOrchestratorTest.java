package pl.netroute.hussar.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.netroute.hussar.core.api.Application;
import pl.netroute.hussar.core.api.EnvironmentConfigurerProvider;
import pl.netroute.hussar.core.api.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class EnvironmentOrchestratorTest {
    private EnvironmentOrchestrator orchestrator;

    @BeforeEach
    public void setup() {
        var propertiesConfigurer = new PropertiesConfigurer();
        var propertiesCleaner = new PropertiesCleaner();

        var executor = Executors.newFixedThreadPool(2);
        var servicesStarter = new ServicesStarter(executor);
        var servicesStopper = new ServicesStopper(executor);

        orchestrator = new EnvironmentOrchestrator(
                propertiesConfigurer,
                propertiesCleaner,
                servicesStarter,
                servicesStopper
        );
    }

    @Test
    public void shouldInitializeEnvironment() {
        // given
        var configurerProvider = new TestEnvironmentConfigurerProvider();

        // when
        orchestrator.initialize(configurerProvider);

        // then
        assertApplicationStarted(configurerProvider.application);
        assertStandaloneServiceStarted(configurerProvider.standaloneServiceA);
        assertStandaloneServiceStarted(configurerProvider.standaloneServiceB);
        assertPropertiesSet(TestEnvironmentConfigurerProvider.PROPERTY_1, TestEnvironmentConfigurerProvider.PROPERTY_VALUE_1);
        assertPropertiesSet(TestEnvironmentConfigurerProvider.PROPERTY_2, TestEnvironmentConfigurerProvider.PROPERTY_VALUE_2);
    }

    @Test
    public void shouldInitializeEnvironmentOnceWhenMultipleThreadsTry() {
        // given
        var configurerProvider = new TestEnvironmentConfigurerProvider();

        // when
        var initializationFutures = IntStream
                .range(0, 10)
                .mapToObj(index -> CompletableFuture.runAsync(() -> orchestrator.initialize(configurerProvider)))
                .collect(Collectors.toUnmodifiableList());

        CompletableFuture
                .allOf(initializationFutures.toArray(new CompletableFuture[0]))
                .join();

        // then
        assertApplicationStarted(configurerProvider.application);
        assertStandaloneServiceStarted(configurerProvider.standaloneServiceA);
        assertStandaloneServiceStarted(configurerProvider.standaloneServiceB);
        assertPropertiesSet(TestEnvironmentConfigurerProvider.PROPERTY_1, TestEnvironmentConfigurerProvider.PROPERTY_VALUE_1);
        assertPropertiesSet(TestEnvironmentConfigurerProvider.PROPERTY_2, TestEnvironmentConfigurerProvider.PROPERTY_VALUE_2);
    }

    private void assertApplicationStarted(Application application) {
        verify(application).start();
    }

    private void assertStandaloneServiceStarted(Service service) {
        verify(service).start();
    }

    private void assertPropertiesSet(String key, String value) {
        assertThat(System.getProperty(key)).isEqualTo(value);
    }

    private static class TestEnvironmentConfigurerProvider implements EnvironmentConfigurerProvider {
        private static final String PROPERTY_1 = "property1";
        private static final String PROPERTY_VALUE_1 = "property_value1";

        private static final String PROPERTY_2 = "property2";
        private static final String PROPERTY_VALUE_2 = "property_value2";

        private final Application application = mock(Application.class);
        private final Service standaloneServiceA = mock(Service.class);
        private final Service standaloneServiceB = mock(Service.class);

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
