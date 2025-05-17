package pl.netroute.hussar.core.network;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import pl.netroute.hussar.core.network.api.NetworkControl;

import java.util.List;

import static org.mockito.Mockito.verify;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class DefaultNetworkRestoreVerifier {
    private final List<NetworkControl> networkControls;

    void verifyNetworkRestored() {
        networkControls.forEach(networkControl -> verify(networkControl).reset());
    }

}