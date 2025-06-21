package pl.netroute.hussar.core.network;

import eu.rekawek.toxiproxy.Proxy;
import eu.rekawek.toxiproxy.model.Toxic;
import eu.rekawek.toxiproxy.model.ToxicDirection;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import pl.netroute.hussar.core.network.api.NetworkScenario;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class ProxyNetworkControlVerifier {
    private static final String LATENCY_FEATURE_NAME = "latency";

    private final List<Proxy> proxies;

    void verifyNetworkEnabled() {
        proxies.forEach(this::verifyProxyEnabled);
    }

    void verifyNetworkDisabled() {
        proxies.forEach(this::verifyProxyDisabled);
    }

    void verifyNetworkDelaySet(@NonNull Duration latency) {
        proxies.forEach(proxy -> verifyProxyDelaySet(proxy, latency));
    }

    void verifyNetworkReset() {
        proxies.forEach(this::verifyProxyReset);
    }

    void verifyNetworkScenarioCreated(@NonNull NetworkScenario scenario) {
        assertThat(scenario).isInstanceOf(DefaultNetworkScenario.class);
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

    private void verifyProxyDelaySet(Proxy proxy, Duration delay) {
        try {
            var toxics = proxy.toxics();

            verify(toxics).latency(LATENCY_FEATURE_NAME, ToxicDirection.DOWNSTREAM, delay.toMillis());
        } catch (Exception ex) {
            throw new AssertionError("Should not happen", ex);
        }
    }

    private void verifyProxyReset(Proxy proxy) {
        verifyProxyEnabled(proxy);

        try {
            var toxics = proxy.toxics();

            toxics.getAll().forEach(this::resetToxic);
        } catch (Exception ex) {
            throw new AssertionError("Should not happen", ex);
        }
    }

    private void resetToxic(Toxic toxic) {
        try {
            toxic.remove();
        } catch (Exception ex) {
            throw new AssertionError("Should not happen", ex);
        }
    }

}
