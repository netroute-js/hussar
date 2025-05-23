package pl.netroute.hussar.core.network;

import eu.rekawek.toxiproxy.ToxiproxyClient;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import pl.netroute.hussar.core.api.Endpoint;
import pl.netroute.hussar.core.network.api.Network;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.verify;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class ProxyNetworkConfigurerVerifier {
    private static final String PROXY_BIND_IP = "0.0.0.0";

    private static final int PROXY_INITIAL_PORT = 8666;
    private static final int PROXY_MAPPED_INITIAL_PORT = 18666;

    private final ToxiproxyClient proxyClient;

    void verifyNetworkConfigured(@NonNull Network network,
                                 @NonNull String networkPrefix,
                                 @NonNull String gatewayAddress,
                                 @NonNull List<Endpoint> internalEndpoints) {
        verifyNetworkPresent(network);
        verifyNetworkControlPresent(network);
        verifyEndpoints(network, gatewayAddress, internalEndpoints);
        verifyProxiesCreated(networkPrefix, gatewayAddress, internalEndpoints);
    }

    private void verifyNetworkPresent(Network network) {
        assertThat(network).isInstanceOf(DefaultNetwork.class);
    }

    private void verifyNetworkControlPresent(Network network) {
        var networkControl = network.getNetworkControl();

        assertThat(networkControl).isInstanceOf(ProxyNetworkControl.class);
    }

    private void verifyEndpoints(Network network, String gatewayHost, List<Endpoint> internalEndpoints) {
        var schema = internalEndpoints.getFirst().scheme();
        var actualEndpoints = network.getEndpoints();
        var expectedEndpoints = IntStream
                .range(0, internalEndpoints.size())
                .mapToObj(index -> Endpoint.of(schema, gatewayHost, PROXY_MAPPED_INITIAL_PORT + index))
                .toList();

        assertThat(actualEndpoints).doesNotContainAnyElementsOf(internalEndpoints);
        assertThat(actualEndpoints).containsExactlyElementsOf(expectedEndpoints);
    }

    private void verifyProxiesCreated(String networkPrefix, String gatewayHost, List<Endpoint> internalEndpoints) {
        var portCounter = new AtomicInteger(PROXY_INITIAL_PORT);

        internalEndpoints.forEach(internalEndpoint -> verifyProxyCreated(networkPrefix, gatewayHost, portCounter.getAndIncrement(), internalEndpoint));
    }

    private void verifyProxyCreated(String networkPrefix, String gatewayHost, int proxyPort, Endpoint internalEndpoint) {
        try {
            var rewritedInternalEndpoint = rewriteInternalEndpoint(gatewayHost, internalEndpoint);
            var proxyEndpoint = Endpoint.of(rewritedInternalEndpoint.scheme(), PROXY_BIND_IP, proxyPort);

            verify(proxyClient).createProxy(startsWith(networkPrefix), eq(proxyEndpoint.hostPort()), eq(rewritedInternalEndpoint.hostPort()));
        } catch (Exception ex) {
            throw new AssertionError("Should not happen", ex);
        }
    }

    private Endpoint rewriteInternalEndpoint(String gatewayHost, Endpoint endpoint) {
        return Optional
                .of(endpoint)
                .filter(Endpoint::isLocalhost)
                .map(actualEndpoint -> Endpoint.of(endpoint.scheme(), gatewayHost, endpoint.port()))
                .orElse(endpoint);
    }

}