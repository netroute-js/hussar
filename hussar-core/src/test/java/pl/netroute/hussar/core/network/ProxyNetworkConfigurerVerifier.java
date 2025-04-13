package pl.netroute.hussar.core.network;

import eu.rekawek.toxiproxy.ToxiproxyClient;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import pl.netroute.hussar.core.api.Endpoint;
import pl.netroute.hussar.core.network.api.Network;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.verify;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class ProxyNetworkConfigurerVerifier {
    private static final String PROXY_BIND_IP = "0.0.0.0";

    private static final int INITIAL_PORT = 20000;

    private final ToxiproxyClient proxyClient;

    void verifyNetworkConfigured(@NonNull Network network,
                                 @NonNull String networkPrefix,
                                 @NonNull List<Endpoint> internalEndpoints) {
        verifyNetworkPresent(network);
        verifyNetworkControlPresent(network);
        verifyEndpoints(network, internalEndpoints);
        verifyProxiesCreated(networkPrefix, internalEndpoints);
    }

    private void verifyNetworkPresent(Network network) {
        assertThat(network).isInstanceOf(DefaultNetwork.class);
    }

    private void verifyNetworkControlPresent(Network network) {
        var networkControl = network.getNetworkControl();

        assertThat(networkControl).isInstanceOf(ProxyNetworkControl.class);
    }

    private void verifyEndpoints(Network network, List<Endpoint> internalEndpoints) {
        var actualEndpoints = network.getEndpoints();

        assertThat(actualEndpoints).doesNotContainAnyElementsOf(internalEndpoints);
    }

    private void verifyProxiesCreated(String networkPrefix, List<Endpoint> internalEndpoints) {
        var portCounter = new AtomicInteger(INITIAL_PORT);

        internalEndpoints.forEach(internalEndpoint -> verifyProxyCreated(networkPrefix, portCounter.getAndIncrement(), internalEndpoint));
    }

    private void verifyProxyCreated(String networkPrefix, int proxyPort, Endpoint internalEndpoint) {
        try {
            var proxyEndpoint = Endpoint.of(internalEndpoint.scheme(), PROXY_BIND_IP, proxyPort);

            verify(proxyClient).createProxy(startsWith(networkPrefix), eq(proxyEndpoint.hostPort()), eq(internalEndpoint.hostPort()));
        } catch (Exception ex) {
            throw new AssertionError("Should not happen", ex);
        }
    }

}