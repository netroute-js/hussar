package pl.netroute.hussar.core.network;

import eu.rekawek.toxiproxy.ToxiproxyClient;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.testcontainers.containers.ToxiproxyContainer;
import pl.netroute.hussar.core.api.InternalUseOnly;
import pl.netroute.hussar.core.network.api.NetworkOperator;

@Slf4j
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
        log.info("Starting ProxyNetworkOperator");

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
        var proxyClient = new ToxiproxyClient(host, port);
        var proxyNetworkConfigurer = new ProxyNetworkConfigurer(toxiproxyContainer, proxyClient);

        networkConfigurer.setNetworkConfigurer(proxyNetworkConfigurer);
    }

    private void resetNetworkConfigurer() {
        networkConfigurer.setNetworkConfigurer(null);
    }

    public static ProxyNetworkOperator newInstance() {
        var toxiproxyContainer = new ToxiproxyContainer(PROXY_DOCKER_IMAGE);

        return new ProxyNetworkOperator(toxiproxyContainer);
    }

}
