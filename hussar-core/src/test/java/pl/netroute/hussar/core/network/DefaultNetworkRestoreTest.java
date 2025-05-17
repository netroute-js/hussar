package pl.netroute.hussar.core.network;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.netroute.hussar.core.network.api.NetworkControl;
import pl.netroute.hussar.core.stub.helper.StubHelper;

import java.util.List;

public class DefaultNetworkRestoreTest {
    private DefaultNetworkRestore networkRestore;
    private DefaultNetworkRestoreVerifier verifier;

    @BeforeEach
    public void setup() {
        var firstNetworkControl = StubHelper.defaultStub(NetworkControl.class);
        var secondNetworkControl = StubHelper.defaultStub(NetworkControl.class);
        var networkControls = List.of(firstNetworkControl, secondNetworkControl);

        networkRestore = new DefaultNetworkRestore(networkControls);
        verifier = new DefaultNetworkRestoreVerifier(networkControls);
    }

    @Test
    public void shouldRestoreNetworkToDefaults() {
        // given
        // when
        networkRestore.restoreToDefault();

        // then
        verifier.verifyNetworkRestored();
    }

}