package pl.netroute.hussar.core.service.test.factory;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.testcontainers.containers.ToxiproxyContainer;
import pl.netroute.hussar.core.network.ProxyNetworkOperator;
import pl.netroute.hussar.core.network.api.NetworkOperator;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NetworkOperatorTestFactory {
    private static final String PROXY_DOCKER_IMAGE = "ghcr.io/shopify/toxiproxy:2.5.0";

    public static NetworkOperator createProxy() {
        var proxyContainer = new ToxiproxyContainer(PROXY_DOCKER_IMAGE);

        return new ProxyNetworkOperator(proxyContainer);
    }

}
