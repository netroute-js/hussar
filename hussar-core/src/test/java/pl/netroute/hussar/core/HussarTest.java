package pl.netroute.hussar.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.netroute.hussar.core.api.application.Application;
import pl.netroute.hussar.core.api.application.HussarApplication;
import pl.netroute.hussar.core.api.application.HussarApplicationRestart;
import pl.netroute.hussar.core.api.environment.Environment;
import pl.netroute.hussar.core.api.environment.EnvironmentConfigurerProvider;
import pl.netroute.hussar.core.api.environment.HussarEnvironment;
import pl.netroute.hussar.core.api.environment.LocalEnvironmentConfigurer;
import pl.netroute.hussar.core.api.service.HussarService;
import pl.netroute.hussar.core.domain.ServiceTestA;
import pl.netroute.hussar.core.domain.ServiceTestB;
import pl.netroute.hussar.core.domain.TestApplication;
import pl.netroute.hussar.core.test.factory.EnvironmentTestFactory;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class HussarTest {
    private EnvironmentConfigurerProviderResolver configurerProviderResolver;
    private EnvironmentOrchestrator environmentOrchestrator;
    private EnvironmentRegistry environmentRegistry;
    private ApplicationRestarter applicationRestarter;
    private AnnotationDetector annotationDetector;
    private Hussar hussar;

    @BeforeEach
    public void setup() {
        configurerProviderResolver = mock(EnvironmentConfigurerProviderResolver.class);
        environmentOrchestrator = mock(EnvironmentOrchestrator.class);
        environmentRegistry = mock(EnvironmentRegistry.class);
        applicationRestarter = spy(ApplicationRestarter.class);
        annotationDetector = spy(AnnotationDetector.class);

        hussar = new Hussar(configurerProviderResolver, environmentOrchestrator, environmentRegistry, applicationRestarter, annotationDetector);
    }

    @Test
    public void shouldInitializeEnvironment() {
        // given
        var testInstance = new ConfigurerAwareTest();
        var environmentConfigurerProvider = new TestEnvironmentConfigurerProvider();
        var environment = EnvironmentTestFactory.create(
                environmentConfigurerProvider.application,
                Set.of(environmentConfigurerProvider.serviceA, environmentConfigurerProvider.serviceB)
        );

        when(configurerProviderResolver.resolve(testInstance)).thenReturn(Optional.of(environmentConfigurerProvider));
        when(environmentOrchestrator.initialize(isA(TestEnvironmentConfigurerProvider.class))).thenReturn(environment);

        // when
        hussar.initializeFor(testInstance);

        // then
        assertEnvironmentInitialized(testInstance, environment, environmentConfigurerProvider);
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
    public void shouldInterceptTestMethodAndRestartApplication() throws Exception {
        // given
        var testInstance = new ConfigurerAwareTest();
        var testMethod = getTestMethod(testInstance, ConfigurerAwareTest.TEST_RESTART_METHOD_NAME);
        var environmentConfigurerProvider = new TestEnvironmentConfigurerProvider();
        var environment = EnvironmentTestFactory.create(
                environmentConfigurerProvider.application,
                Set.of(environmentConfigurerProvider.serviceA, environmentConfigurerProvider.serviceB)
        );

        when(environmentRegistry.getEntry(testInstance)).thenReturn(Optional.of(environment));

        // when
        hussar.interceptTest(testInstance, testMethod);

        // then
        assertMethodScannedForApplicationRestart(testMethod);
        assertApplicationRestarted(environment);
    }

    @Test
    public void shouldInterceptTestMethodAndSkipRestartingApplication() throws Exception{
        // given
        var testInstance = new ConfigurerAwareTest();
        var testMethod = getTestMethod(testInstance, ConfigurerAwareTest.TEST_METHOD_NAME);
        var environmentConfigurerProvider = new TestEnvironmentConfigurerProvider();
        var environment = EnvironmentTestFactory.create(
                environmentConfigurerProvider.application,
                Set.of(environmentConfigurerProvider.serviceA, environmentConfigurerProvider.serviceB)
        );

        when(environmentRegistry.getEntry(testInstance)).thenReturn(Optional.of(environment));

        // when
        hussar.interceptTest(testInstance, testMethod);

        // then
        assertMethodScannedForApplicationRestart(testMethod);
        assertApplicationNotRestarted();
    }

    @Test
    public void shouldSilentlySkipInterceptingTestMethodWhenTestObjectIsNotHussarAware() throws Exception {
        // given
        var testInstance = new NoConfigurerAwareTest();
        var testMethod = getTestMethod(testInstance, NoConfigurerAwareTest.TEST_METHOD_NAME);

        // when
        hussar.interceptTest(testInstance, testMethod);

        // then
        assertNoMethodScanning();
        assertApplicationNotRestarted();
    }

    @Test
    public void shouldShutdownEnvironments() {
        // given
        // when
        hussar.shutdown();

        // then
        assertEnvironmentsShutdown();
    }

    private void assertEnvironmentInitialized(ConfigurerAwareTest testInstance,
                                              Environment environment,
                                              TestEnvironmentConfigurerProvider environmentConfigurerProvider) {
        verify(environmentOrchestrator).initialize(environmentConfigurerProvider);
        verify(environmentRegistry).register(testInstance, environment);

        assertThat(testInstance.application).isEqualTo(environmentConfigurerProvider.application);
        assertThat(testInstance.serviceA).isEqualTo(environmentConfigurerProvider.serviceA);
        assertThat(testInstance.serviceB).isEqualTo(environmentConfigurerProvider.serviceB);
    }

    private void assertEnvironmentInitializationSkipped() {
        verify(environmentOrchestrator, never()).initialize(any());
    }

    private void assertEnvironmentsShutdown() {
        verify(environmentOrchestrator).shutdown();
        verify(environmentRegistry).deleteEntries();
    }

    private void assertMethodScannedForApplicationRestart(Method testMethod) {
        verify(annotationDetector).detect(eq(testMethod), eq(HussarApplicationRestart.class), any());
    }

    private void assertApplicationRestarted(Environment environment) {
        verify(applicationRestarter).restart(environment.application());
    }

    private void assertApplicationNotRestarted() {
        verify(applicationRestarter, never()).restart(any());
    }

    private void assertNoMethodScanning() {
        verify(annotationDetector, never()).detect(any(), any(), any());
    }

    private Method getTestMethod(Object testInstance, String methodName) throws NoSuchMethodException {
        return testInstance.getClass().getMethod(methodName);
    }

    static class NoConfigurerAwareTest {
        private static final String TEST_METHOD_NAME = "test";

        @Test
        public void test() {
        }

    }

    @HussarEnvironment(configurerProvider = TestEnvironmentConfigurerProvider.class)
    static class ConfigurerAwareTest {
        private static final String TEST_METHOD_NAME = "test";
        private static final String TEST_RESTART_METHOD_NAME = "testRestart";

        @HussarApplication
        Application application;

        @HussarService
        ServiceTestA serviceA;

        @HussarService
        ServiceTestB serviceB;

        @Test
        public void test() {
        }

        @Test
        @HussarApplicationRestart
        public void testRestart() {
        }

    }

    static class TestEnvironmentConfigurerProvider implements EnvironmentConfigurerProvider {
        private static final String PROPERTY_1 = "some.property";
        private static final String PROPERTY_VALUE_1 = "some_property_value";

        private static final String ENV_VARIABLE_1 = "SOME_ENV_VARIABLE";
        private static final String ENV_VARIABLE_VALUE_1 = "some_env_variable_value";

        private final Application application;
        private final ServiceTestA serviceA;
        private final ServiceTestB serviceB;

        public TestEnvironmentConfigurerProvider() {
            this.application = new TestApplication();
            this.serviceA = new ServiceTestA();
            this.serviceB = new ServiceTestB();
        }

        @Override
        public LocalEnvironmentConfigurer provide() {
            return LocalEnvironmentConfigurer
                    .newInstance()
                    .withProperty(PROPERTY_1, PROPERTY_VALUE_1)
                    .withEnvironmentVariable(ENV_VARIABLE_1, ENV_VARIABLE_VALUE_1)
                    .withApplication(application)
                    .withService(serviceA)
                    .withService(serviceB)
                    .done();
        }

    }
}
