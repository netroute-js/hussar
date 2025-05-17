package pl.netroute.hussar.core;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import pl.netroute.hussar.core.network.api.NetworkRestore;
import pl.netroute.hussar.core.service.api.ServiceRegistry;
import pl.netroute.hussar.core.test.factory.NetworkRestoreTestFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class NetworkRestoreVerifier {
    private final ServiceRegistry serviceRegistry;

    void verifyNetworkRestoreInjected(@NonNull NetworkRestore networkRestore) {
        var expectedNetworkRestore = NetworkRestoreTestFactory.create(serviceRegistry);

        assertThat(networkRestore).isEqualTo(expectedNetworkRestore);
    }

    void verifySkippedNetworkRestoreInjection() {
        serviceRegistry
                .getEntries()
                .forEach(service -> verify(service, never()).getNetworkControl());
    }

}