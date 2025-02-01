package pl.netroute.hussar.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.netroute.hussar.core.application.api.Application;
import pl.netroute.hussar.core.application.ApplicationStartupContext;
import pl.netroute.hussar.core.configuration.api.ConfigurationEntry;
import pl.netroute.hussar.core.domain.ServiceTestA;
import pl.netroute.hussar.core.domain.ServiceTestB;
import pl.netroute.hussar.core.domain.StubServiceConfigurer;
import pl.netroute.hussar.core.environment.api.Environment;
import pl.netroute.hussar.core.environment.api.EnvironmentConfigurerProvider;
import pl.netroute.hussar.core.environment.api.LocalEnvironmentConfigurer;
import pl.netroute.hussar.core.service.api.Service;
import pl.netroute.hussar.core.service.ServiceStartupContext;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static pl.netroute.hussar.core.configuration.api.ConfigurationEntry.envVariable;
import static pl.netroute.hussar.core.configuration.api.ConfigurationEntry.property;

public class EnvironmentOrchestratorTest {
    private EnvironmentOrchestrator orchestrator;

    @BeforeEach
    public void setup() {
        var executor = Executors.newFixedThreadPool(2);
        var servicesStarter = new ServiceStarter(executor);
        var servicesStopper = new ServiceStopper(executor);

        orchestrator = new EnvironmentOrchestrator(
                servicesStarter,
                servicesStopper
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
        assertServiceStarted(configurerProvider.standaloneServiceA.getService());
        assertServiceStarted(configurerProvider.standaloneServiceB.getService());
    }

    @Test
    public void shouldInitializeEnvironmentOnceWhenMultipleThreadsTry() {
        // given
        var configurerProvider = new TestEnvironmentConfigurerProvider();

        // when
        var initializationFutures = IntStream
                .range(0, 10)
                .mapToObj(index -> CompletableFuture.supplyAsync(() -> orchestrator.initialize(configurerProvider)))
                .toList();

        CompletableFuture
                .allOf(initializationFutures.toArray(new CompletableFuture[0]))
                .join();

        // then
        initializationFutures
                .stream()
                .map(CompletableFuture::join)
                .forEach(this::assertEnvironmentInitialized);

        assertApplicationStarted(configurerProvider.application);
        assertServiceStarted(configurerProvider.standaloneServiceA.getService());
        assertServiceStarted(configurerProvider.standaloneServiceB.getService());
    }

    private void assertEnvironmentInitialized(Environment environment) {
        assertThat(environment).isNotNull();
    }

    private void assertApplicationStarted(Application application) {
        var property = property(TestEnvironmentConfigurerProvider.PROPERTY_1, TestEnvironmentConfigurerProvider.PROPERTY_VALUE_1);
        var envVariable = envVariable(TestEnvironmentConfigurerProvider.ENV_VARIABLE_1, TestEnvironmentConfigurerProvider.ENV_VARIABLE_VALUE_1);
        var externalConfigurations = Set.<ConfigurationEntry>of(property, envVariable);

        var context = new ApplicationStartupContext(externalConfigurations);

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

        private Application application;
        private StubServiceConfigurer<ServiceTestA> standaloneServiceA;
        private StubServiceConfigurer<ServiceTestB> standaloneServiceB;

        @Override
        public LocalEnvironmentConfigurer provide() {
            this.application = mock(Application.class);
            this.standaloneServiceA = new StubServiceConfigurer<>(ServiceTestA.class);
            this.standaloneServiceB = new StubServiceConfigurer<>(ServiceTestB.class);

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
