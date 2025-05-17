package pl.netroute.hussar.core;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import pl.netroute.hussar.core.environment.api.Environment;
import pl.netroute.hussar.core.service.api.Service;
import pl.netroute.hussar.core.test.HussarAwareTest;
import pl.netroute.hussar.core.test.TestEnvironmentConfigurerProvider;
import pl.netroute.hussar.core.test.factory.NetworkRestoreTestFactory;
import pl.netroute.hussar.core.test.stub.ServiceStubA;
import pl.netroute.hussar.core.test.stub.ServiceStubB;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class HussarVerifier {
    private final EnvironmentOrchestrator environmentOrchestrator;
    private final EnvironmentRegistry environmentRegistry;
    private final ApplicationRestarter applicationRestarter;

    void verifyEnvironmentInitialized(HussarAwareTest testInstance,
                                      Environment environment) {
        var application = environment.getApplication();
        var serviceA = findService(ServiceStubA.class, environment);
        var serviceB = findService(ServiceStubB.class, environment);
        var networkRestore = NetworkRestoreTestFactory.create(environment.getServiceRegistry());

        verify(environmentOrchestrator).initialize(isA(TestEnvironmentConfigurerProvider.class));
        verify(environmentRegistry).register(testInstance, environment);

        assertThat(testInstance.application).isEqualTo(application);
        assertThat(testInstance.serviceA).isEqualTo(serviceA);
        assertThat(testInstance.serviceB).isEqualTo(serviceB);

        assertThat(testInstance.networkRestore).isEqualTo(networkRestore);
    }

    void verifyEnvironmentInitializationSkipped() {
        verify(environmentOrchestrator, never()).initialize(any());
    }

    void verifyEnvironmentsShutdown() {
        verify(environmentOrchestrator).shutdown();
        verify(environmentRegistry).deleteEntries();
    }

    void verifyApplicationRestarted(@NonNull Environment environment) {
        var application = environment.getApplication();

        verify(application).restart();
        verify(applicationRestarter).restart(application);
    }

    void verifyApplicationNotRestarted() {
        verify(applicationRestarter, never()).restart(any());
    }

    private <T extends Service> T findService(Class<T> serviceType, Environment environment) {
        return (T) environment
                .getServiceRegistry()
                .findEntryByType(serviceType)
                .orElseThrow(() -> new IllegalStateException("Could not find Service"));
    }

}
