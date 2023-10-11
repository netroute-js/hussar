package pl.netroute.hussar.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.netroute.hussar.core.api.Application;
import pl.netroute.hussar.core.api.ApplicationStartupContext;
import pl.netroute.hussar.core.api.ConfigurationEntry;
import pl.netroute.hussar.core.api.EnvironmentConfigurerProvider;
import pl.netroute.hussar.core.api.Service;
import pl.netroute.hussar.core.api.ServiceStartupContext;
import pl.netroute.hussar.core.domain.ServiceTestA;
import pl.netroute.hussar.core.domain.ServiceTestB;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class EnvironmentOrchestratorTest {
    private EnvironmentOrchestrator orchestrator;

    @BeforeEach
    public void setup() {
        var executor = Executors.newFixedThreadPool(2);
        var servicesStarter = new ServiceStarter(executor);
        var servicesStopper = new ServiceStopper(executor);

        var applicationConfigurationFlattener = new ApplicationConfigurationFlattener();
        var applicationConfigurationLoader = new ApplicationConfigurationLoader(applicationConfigurationFlattener);
        var applicationConfigurationResolver = new ApplicationConfigurationResolver(applicationConfigurationLoader, applicationConfigurationFlattener);

        orchestrator = new EnvironmentOrchestrator(
                servicesStarter,
                servicesStopper,
                applicationConfigurationResolver
        );
    }

    @Test
    public void shouldInitializeEnvironment() {
        // given
        var configurerProvider = new TestEnvironmentConfigurerProvider();

        // when
        var environment = orchestrator.initialize(configurerProvider);

        // then
        assertEnvironmentInitialized(environment);
        assertApplicationStarted(configurerProvider.application);
        assertServiceStarted(configurerProvider.standaloneServiceA);
        assertServiceStarted(configurerProvider.standaloneServiceB);
    }

    @Test
    public void shouldInitializeEnvironmentOnceWhenMultipleThreadsTry() {
        // given
        var configurerProvider = new TestEnvironmentConfigurerProvider();

        // when
        var initializationFutures = IntStream
                .range(0, 10)
                .mapToObj(index -> CompletableFuture.supplyAsync(() -> orchestrator.initialize(configurerProvider)))
                .collect(Collectors.toUnmodifiableList());

        CompletableFuture
                .allOf(initializationFutures.toArray(new CompletableFuture[0]))
                .join();

        // then
        initializationFutures
                .stream()
                .map(CompletableFuture::join)
                .forEach(this::assertEnvironmentInitialized);

        assertApplicationStarted(configurerProvider.application);
        assertServiceStarted(configurerProvider.standaloneServiceA);
        assertServiceStarted(configurerProvider.standaloneServiceB);
    }

    private void assertEnvironmentInitialized(Environment environment) {
        assertThat(environment).isNotNull();
    }

    private void assertApplicationStarted(Application application) {
        var configurationMap = Map.<String, Object>of(
                TestEnvironmentConfigurerProvider.PROPERTY_1, TestEnvironmentConfigurerProvider.PROPERTY_VALUE_1
        );

        var context = new ApplicationStartupContext(configurationMap);

        verify(application).start(context);
    }

    private void assertServiceStarted(Service service) {
        verify(service).start(isA(ServiceStartupContext.class));
    }

    private static class TestEnvironmentConfigurerProvider implements EnvironmentConfigurerProvider {
        private static final String PROPERTY_1 = "some.property";
        private static final String PROPERTY_VALUE_1 = "some_property_value";

        private static final String ENV_VARIABLE_1 = "SOME_ENV_VARIABLE";
        private static final String ENV_VARIABLE_VALUE_1 = "some_env_variable_value";

        private final Application application = mock(Application.class);
        private final ServiceTestA standaloneServiceA = mock(ServiceTestA.class);
        private final ServiceTestB standaloneServiceB = mock(ServiceTestB.class);

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
