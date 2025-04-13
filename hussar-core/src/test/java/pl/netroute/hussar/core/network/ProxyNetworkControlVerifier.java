package pl.netroute.hussar.core.network;

import eu.rekawek.toxiproxy.Proxy;
import eu.rekawek.toxiproxy.model.ToxicDirection;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.time.Duration;
import java.util.List;

import static org.mockito.Mockito.verify;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class ProxyNetworkControlVerifier {
    private static final String BANDWIDTH_FEATURE_NAME = "bandwidth";
    private static final String LATENCY_FEATURE_NAME = "latency";

    private final List<Proxy> proxies;

    void verifyNetworkEnabled() {
        proxies.forEach(this::verifyProxyEnabled);
    }

    void verifyNetworkDisabled() {
        proxies.forEach(this::verifyProxyDisabled);
    }

    void verifyNetworkBandwidthSet(long bandwidth) {
        proxies.forEach(proxy -> verifyProxyBandwidthSet(proxy, bandwidth));
    }

    void verifyNetworkDelaySet(@NonNull Duration latency) {
        proxies.forEach(proxy -> verifyProxyDelaySet(proxy, latency));
    }

    private void verifyProxyEnabled(Proxy proxy) {
        try {
            verify(proxy).enable();
        } catch (Exception ex) {
            throw new AssertionError("Should not happen", ex);
        }
    }

    private void verifyProxyDisabled(Proxy proxy) {
        try {
            verify(proxy).disable();
        } catch (Exception ex) {
            throw new AssertionError("Should not happen", ex);
        }
    }

    private void verifyProxyBandwidthSet(Proxy proxy, long bandwidth) {
        try {
            var toxics = proxy.toxics();

            verify(toxics).bandwidth(BANDWIDTH_FEATURE_NAME, ToxicDirection.DOWNSTREAM, bandwidth);
        } catch (Exception ex) {
            throw new AssertionError("Should not happen", ex);
        }
    }

    private void verifyProxyDelaySet(Proxy proxy, Duration delay) {
        try {
            var toxics = proxy.toxics();

            verify(toxics).latency(LATENCY_FEATURE_NAME, ToxicDirection.DOWNSTREAM, delay.toMillis());
        } catch (Exception ex) {
            throw new AssertionError("Should not happen", ex);
        }
    }

}
