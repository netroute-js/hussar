package pl.netroute.hussar.core.network;

import eu.rekawek.toxiproxy.ToxiproxyClient;
import lombok.Getter;
import lombok.NonNull;
import org.testcontainers.containers.ToxiproxyContainer;
import pl.netroute.hussar.core.api.InternalUseOnly;
import pl.netroute.hussar.core.docker.DockerHostResolver;
import pl.netroute.hussar.core.docker.api.DockerNetwork;
import pl.netroute.hussar.core.network.api.NetworkOperator;

@InternalUseOnly
public class ProxyNetworkOperator implements NetworkOperator {
    private static final String PROXY_DOCKER_IMAGE = "ghcr.io/shopify/toxiproxy:2.5.0";

    @NonNull
    private final ToxiproxyContainer toxiproxyContainer;

    @Getter
    @NonNull
    private final LazyNetworkConfigurer networkConfigurer;

    public ProxyNetworkOperator(@NonNull ToxiproxyContainer toxiproxyContainer) {
        this.toxiproxyContainer = toxiproxyContainer;
        this.networkConfigurer = new LazyNetworkConfigurer();
    }

    @Override
    public void start(@NonNull NetworkOperatorStartupContext context) {
        toxiproxyContainer.start();

        initializeNetworkConfigurer(toxiproxyContainer);
    }

    @Override
    public void shutdown() {
        toxiproxyContainer.close();

        resetNetworkConfigurer();
    }

    private void initializeNetworkConfigurer(ToxiproxyContainer toxiproxyContainer) {
        var host = toxiproxyContainer.getHost();
        var port = toxiproxyContainer.getControlPort();
        var dockerHostResolver = new DockerHostResolver();
        var proxyClient = new ToxiproxyClient(host, port);
        var proxyNetworkConfigurer = new ProxyNetworkConfigurer(toxiproxyContainer, proxyClient, dockerHostResolver);

        networkConfigurer.setNetworkConfigurer(proxyNetworkConfigurer);
    }

    private void resetNetworkConfigurer() {
        networkConfigurer.setNetworkConfigurer(null);
    }

    public static ProxyNetworkOperator newInstance(@NonNull DockerNetwork dockerNetwork) {
        var network = dockerNetwork.network();
        var toxiproxyContainer = new ToxiproxyContainer(PROXY_DOCKER_IMAGE).withNetwork(network);

        return new ProxyNetworkOperator(toxiproxyContainer);
    }

}
