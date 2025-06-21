package pl.netroute.hussar.core.network;

import eu.rekawek.toxiproxy.Proxy;
import eu.rekawek.toxiproxy.model.Toxic;
import eu.rekawek.toxiproxy.model.ToxicDirection;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import pl.netroute.hussar.core.api.InternalUseOnly;
import pl.netroute.hussar.core.network.api.NetworkControl;
import pl.netroute.hussar.core.network.api.NetworkScenario;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

@InternalUseOnly
@RequiredArgsConstructor
public class ProxyNetworkControl implements NetworkControl {
    private static final String LATENCY_FEATURE_NAME = "latency";

    @NonNull
    private final List<Proxy> proxies;

    @Override
    public void enable() {
        proxies.forEach(this::enableProxy);
    }

    @Override
    public void disable() {
        proxies.forEach(this::disableProxy);
    }

    @Override
    public void delay(@NonNull Duration delay) {
        proxies.forEach(proxy -> setProxyDelay(proxy, delay));
    }

    @Override
    public void reset() {
        proxies.forEach(this::resetProxy);
    }

    @Override
    public NetworkScenario scenario() {
        return new DefaultNetworkScenario(this);
    }

    private void enableProxy(Proxy proxy) {
        try {
            proxy.enable();
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to enable proxy", ex);
        }
    }

    private void disableProxy(Proxy proxy) {
        try {
            proxy.disable();
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to enable proxy", ex);
        }
    }

    private void setProxyDelay(Proxy proxy, Duration delay) {
        try {
            proxy.toxics()
                 .latency(LATENCY_FEATURE_NAME, ToxicDirection.DOWNSTREAM, delay.toMillis());
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to set bandwidth", ex);
        }
    }

    private void resetProxy(Proxy proxy) {
        enableProxy(proxy);

        try {
            proxy.toxics()
                 .getAll()
                 .forEach(this::resetToxic);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to reset proxy", ex);
        }
    }

    private void resetToxic(Toxic toxic) {
        try {
            toxic.remove();
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to reset toxic", ex);
        }
    }

}
