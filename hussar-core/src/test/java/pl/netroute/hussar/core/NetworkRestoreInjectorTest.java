package pl.netroute.hussar.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.netroute.hussar.core.network.api.NetworkRestore;
import pl.netroute.hussar.core.network.api.HussarNetworkRestore;
import pl.netroute.hussar.core.test.factory.ServiceRegistryTestFactory;

public class NetworkRestoreInjectorTest {
    private NetworkRestoreInjector networkRestoreInjector;
    private NetworkRestoreVerifier verifier;

    @BeforeEach
    public void setup() {
        var serviceRegistry = ServiceRegistryTestFactory.create();

        networkRestoreInjector = new NetworkRestoreInjector(serviceRegistry);
        verifier = new NetworkRestoreVerifier(serviceRegistry);
    }

    @Test
    public void shouldInjectNetworkRestore() {
        // given
        var testInstance = new TestClassWithNetworkRestores();

        // when
        networkRestoreInjector.inject(testInstance);

        // then
        verifier.verifyNetworkRestoreInjected(testInstance.networkRestore);
    }

    @Test
    public void shouldInjectNetworkRestoreWhenInheritancePresent() {
        // given
        var testInstance = new SubTestClassWithNetworkRestores();

        // when
        networkRestoreInjector.inject(testInstance);

        // then
        verifier.verifyNetworkRestoreInjected(testInstance.baseNetworkRestore);
        verifier.verifyNetworkRestoreInjected(testInstance.networkRestore);
    }

    @Test
    public void shouldSkipInjectingNetworkRestoreWhenNoAnnotatedFields() {
        // given
        var testInstance = new TestClassWithoutNetworkRestores();

        // when
        networkRestoreInjector.inject(testInstance);

        // then
        verifier.verifySkippedNetworkRestoreInjection();
    }

    private static class TestClassWithoutNetworkRestores {
    }

    private static class TestClassWithNetworkRestores {

        @HussarNetworkRestore
        NetworkRestore networkRestore;

    }

    private static class BaseTestClassWithNetworkRestores {

        @HussarNetworkRestore
        NetworkRestore baseNetworkRestore;

    }

    private static class SubTestClassWithNetworkRestores extends BaseTestClassWithNetworkRestores {

        @HussarNetworkRestore
        private NetworkRestore networkRestore;

    }

}