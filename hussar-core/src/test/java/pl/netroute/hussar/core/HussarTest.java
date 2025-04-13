package pl.netroute.hussar.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.netroute.hussar.core.test.HussarAwareTest;
import pl.netroute.hussar.core.test.PlainTest;
import pl.netroute.hussar.core.test.TestEnvironmentConfigurerProvider;
import pl.netroute.hussar.core.test.stub.EnvironmentStub;

import java.lang.reflect.Method;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class HussarTest {
    private EnvironmentOrchestrator environmentOrchestrator;
    private EnvironmentRegistry environmentRegistry;
    private Hussar hussar;
    private HussarVerifier verifier;

    @BeforeEach
    public void setup() {
        var configurerProviderResolver = spy(EnvironmentConfigurerProviderResolver.class);
        var applicationRestarter = spy(ApplicationRestarter.class);
        var annotationDetector = spy(AnnotationDetector.class);

        environmentOrchestrator = mock(EnvironmentOrchestrator.class);
        environmentRegistry = spy(EnvironmentRegistry.class);

        hussar = new Hussar(configurerProviderResolver, environmentOrchestrator, environmentRegistry, applicationRestarter, annotationDetector);

        verifier = new HussarVerifier(environmentOrchestrator, environmentRegistry, applicationRestarter);
    }

    @Test
    public void shouldInitializeEnvironment() {
        // given
        var testInstance = new HussarAwareTest();
        var environment = EnvironmentStub.defaultStub();

        when(environmentOrchestrator.initialize(isA(TestEnvironmentConfigurerProvider.class))).thenReturn(environment);

        // when
        hussar.initializeFor(testInstance);

        // then
        verifier.verifyEnvironmentInitialized(testInstance, environment);
    }

    @Test
    public void shouldSkipInitializingEnvironmentWhenTestObjectIsNotHussarAware() {
        // given
        var testInstance = new PlainTest();

        // when
        hussar.initializeFor(testInstance);

        // then
        verifier.verifyEnvironmentInitializationSkipped();
    }

    @Test
    public void shouldInterceptTestMethodAndRestartApplication() throws Exception {
        // given
        var testInstance = new HussarAwareTest();
        var testMethod = getTestMethod(testInstance, HussarAwareTest.TEST_RESTART_METHOD_NAME);
        var environment = EnvironmentStub.defaultStub();

        when(environmentRegistry.getEntry(testInstance)).thenReturn(Optional.of(environment));

        // when
        hussar.interceptTest(testInstance, testMethod);

        // then
        verifier.verifyApplicationRestarted(environment);
    }

    @Test
    public void shouldInterceptTestMethodAndSkipRestartingApplication() throws Exception{
        // given
        var testInstance = new HussarAwareTest();
        var testMethod = getTestMethod(testInstance, HussarAwareTest.TEST_METHOD_NAME);
        var environment = EnvironmentStub.defaultStub();

        when(environmentRegistry.getEntry(testInstance)).thenReturn(Optional.of(environment));

        // when
        hussar.interceptTest(testInstance, testMethod);

        // then
        verifier.verifyApplicationNotRestarted();
    }

    @Test
    public void shouldSilentlySkipInterceptingTestMethodWhenTestObjectIsNotHussarAware() throws Exception {
        // given
        var testInstance = new PlainTest();
        var testMethod = getTestMethod(testInstance, PlainTest.TEST_METHOD_NAME);

        // when
        hussar.interceptTest(testInstance, testMethod);

        // then
        verifier.verifyApplicationNotRestarted();
    }

    @Test
    public void shouldShutdownEnvironments() {
        // given
        // when
        hussar.shutdown();

        // then
        verifier.verifyEnvironmentsShutdown();
    }

    private Method getTestMethod(Object testInstance, String methodName) throws NoSuchMethodException {
        return testInstance.getClass().getMethod(methodName);
    }

}
