package pl.netroute.hussar.core.network;

import eu.rekawek.toxiproxy.Proxy;
import eu.rekawek.toxiproxy.model.Toxic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.netroute.hussar.core.stub.helper.StubHelper;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

import static org.mockito.Mockito.when;

public class ProxyNetworkControlTest {
    private Proxy proxyA;
    private Proxy proxyB;

    private ProxyNetworkControl networkControl;
    private ProxyNetworkControlVerifier verifier;

    @BeforeEach
    public void setup() {
        proxyA = StubHelper.defaultStub(Proxy.class);
        proxyB = StubHelper.defaultStub(Proxy.class);
        var proxies = List.of(proxyA, proxyB);

        networkControl = new ProxyNetworkControl(proxies);
        verifier = new ProxyNetworkControlVerifier(proxies);
    }

    @Test
    public void shouldEnableNetwork() {
        // given
        // when
        networkControl.enable();

        // then
        verifier.verifyNetworkEnabled();
    }

    @Test
    public void shouldDisableNetwork() {
        // given
        // when
        networkControl.disable();

        // then
        verifier.verifyNetworkDisabled();
    }

    @Test
    public void shouldSetNetworkDelay() {
        // given
        var delay = Duration.ofMillis(500L);

        // when
        networkControl.delay(delay);

        // then
        verifier.verifyNetworkDelaySet(delay);
    }

    @Test
    public void shouldResetNetwork() throws IOException {
        // given
        var proxyToxicA = StubHelper.defaultStub(Toxic.class);
        var proxyToxicB = StubHelper.defaultStub(Toxic.class);

        when(proxyA.toxics().getAll()).thenAnswer(inv -> List.of(proxyToxicA));
        when(proxyB.toxics().getAll()).thenAnswer(inv -> List.of(proxyToxicB));

        // when
        networkControl.reset();

        // then
        verifier.verifyNetworkReset();
    }

}
