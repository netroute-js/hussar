package pl.netroute.hussar.core.network;

import eu.rekawek.toxiproxy.Proxy;
import eu.rekawek.toxiproxy.model.ToxicDirection;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import pl.netroute.hussar.core.api.InternalUseOnly;
import pl.netroute.hussar.core.network.api.NetworkControl;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

@InternalUseOnly
@RequiredArgsConstructor
public class ProxyNetworkControl implements NetworkControl {
    private static final String BANDWIDTH_FEATURE_NAME = "bandwidth";
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
    public void bandwidth(long kilobytesPerSecond) {
        proxies.forEach(proxy -> setProxyBandwidth(proxy, kilobytesPerSecond));
    }

    @Override
    public void delay(@NonNull Duration delay) {
        proxies.forEach(proxy -> setProxyDelay(proxy, delay));
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

    private void setProxyBandwidth(Proxy proxy, long kilobytesPerSecond) {
        try {
            proxy
                    .toxics()
                    .bandwidth(BANDWIDTH_FEATURE_NAME, ToxicDirection.DOWNSTREAM, kilobytesPerSecond);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to set bandwidth", ex);
        }
    }

    private void setProxyDelay(Proxy proxy, Duration delay) {
        try {
            proxy
                    .toxics()
                    .latency(LATENCY_FEATURE_NAME, ToxicDirection.DOWNSTREAM, delay.toMillis());
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to set bandwidth", ex);
        }
    }

}
