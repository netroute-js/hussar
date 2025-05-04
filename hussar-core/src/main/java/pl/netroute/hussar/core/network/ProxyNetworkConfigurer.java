package pl.netroute.hussar.core.network;

import eu.rekawek.toxiproxy.Proxy;
import eu.rekawek.toxiproxy.ToxiproxyClient;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.testcontainers.containers.ToxiproxyContainer;
import pl.netroute.hussar.core.api.Endpoint;
import pl.netroute.hussar.core.api.InternalUseOnly;
import pl.netroute.hussar.core.docker.DockerHostResolver;
import pl.netroute.hussar.core.network.api.Network;
import pl.netroute.hussar.core.network.api.NetworkConfigurer;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@InternalUseOnly
public class ProxyNetworkConfigurer implements NetworkConfigurer {
    private static final String PROXY_BIND_IP = "0.0.0.0";

    private static final int PROXY_INITIAL_PORT = 8666;

    private final ToxiproxyContainer toxiproxyContainer;
    private final ToxiproxyClient toxiproxyClient;
    private final AtomicInteger toxiproxyPortCounter;

    public ProxyNetworkConfigurer(@NonNull ToxiproxyContainer toxiproxyContainer,
                                  @NonNull ToxiproxyClient toxiproxyClient) {
        this.toxiproxyContainer = toxiproxyContainer;
        this.toxiproxyClient = toxiproxyClient;
        this.toxiproxyPortCounter = new AtomicInteger(PROXY_INITIAL_PORT);
    }

    @Override
    public Network configure(@NonNull String networkPrefix, @NonNull List<Endpoint> endpoints) {
        var proxiesMetadata = configureProxies(networkPrefix, endpoints);
        var proxyEndpoints = extractProxyEndpoints(proxiesMetadata);
        var networkControl = createNetworkControl(proxiesMetadata);

        return new DefaultNetwork(proxyEndpoints, networkControl);
    }

    private List<ProxyMetadata> configureProxies(String networkPrefix, List<Endpoint> endpoints) {
        return endpoints
                .stream()
                .map(this::rewriteUpstreamEndpoint)
                .map(endpoint -> configureProxy(networkPrefix, endpoint))
                .toList();
    }

    private List<Endpoint> extractProxyEndpoints(List<ProxyMetadata> proxies) {
        return proxies
                .stream()
                .map(ProxyMetadata::proxyEndpoint)
                .toList();
    }

    private ProxyNetworkControl createNetworkControl(List<ProxyMetadata> proxiesMetadata) {
        var proxies = proxiesMetadata
                .stream()
                .map(ProxyMetadata::proxy)
                .toList();

        return new ProxyNetworkControl(proxies);
    }

    private ProxyMetadata configureProxy(String proxyPrefix, Endpoint upstreamEndpoint) {
        try {
            var proxyName = ProxyNameResolver.resolve(proxyPrefix);
            var proxyScheme = upstreamEndpoint.scheme();

            var internalProxyPort = toxiproxyPortCounter.getAndIncrement();
            var internalProxyEndpoint = Endpoint.of(proxyScheme, PROXY_BIND_IP, internalProxyPort);

            var proxyPort = toxiproxyContainer.getMappedPort(internalProxyPort);
            var proxyEndpoint = Endpoint.of(proxyScheme, PROXY_BIND_IP, proxyPort);
            var proxy = toxiproxyClient.createProxy(proxyName, internalProxyEndpoint.hostPort(), upstreamEndpoint.hostPort());

            return new ProxyMetadata(proxy, proxyEndpoint, upstreamEndpoint);
        } catch (IOException ex) {
            throw new IllegalStateException("Could not create Proxy", ex);
        }
    }

    private Endpoint rewriteUpstreamEndpoint(Endpoint upstreamEndpoint) {
        return Optional
                .of(upstreamEndpoint)
                .filter(Endpoint::isLocalhost)
                .map(endpoint -> Endpoint.of(endpoint.scheme(), DockerHostResolver.DOCKER_BRIDGE_HOST, endpoint.port()))
                .orElse(upstreamEndpoint);
    }

    private record ProxyMetadata(@NonNull Proxy proxy,
                                 @NonNull Endpoint proxyEndpoint,
                                 @NonNull Endpoint upstreamEndpoint) {
    }

}
