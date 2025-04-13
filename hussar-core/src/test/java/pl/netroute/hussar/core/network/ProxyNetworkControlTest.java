package pl.netroute.hussar.core.network;

import eu.rekawek.toxiproxy.Proxy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.netroute.hussar.core.test.stub.Mock;

import java.time.Duration;
import java.util.List;

public class ProxyNetworkControlTest {
    private ProxyNetworkControl networkControl;
    private ProxyNetworkControlVerifier verifier;

    @BeforeEach
    public void setup() {
        var proxyA = Mock.defaultMock(Proxy.class);
        var proxyB = Mock.defaultMock(Proxy.class);
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
    public void shouldSetNetworkBandwidth() {
        // given
        var kiloBytesPerSec = 1024L;

        // when
        networkControl.bandwidth(kiloBytesPerSec);

        // then
        verifier.verifyNetworkBandwidthSet(kiloBytesPerSec);
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

}
