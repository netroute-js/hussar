package pl.netroute.hussar.core.network;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.testcontainers.containers.ToxiproxyContainer;
import pl.netroute.hussar.core.network.api.NetworkConfigurer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.type;
import static org.mockito.Mockito.verify;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class ProxyNetworkOperatorVerifier {
    private final ToxiproxyContainer proxyContainer;

    void verifyNetworkOperatorStarted() {
        verify(proxyContainer).start();
    }

    void verifyNetworkOperatorStopped() {
        verify(proxyContainer).close();
    }

    void verifyNetworkConfigurerNotInitialized(NetworkConfigurer networkConfigurer) {
        assertThat(networkConfigurer)
                .asInstanceOf(type(LazyNetworkConfigurer.class))
                .satisfies(lazyNetworkConfigurer -> assertThat(lazyNetworkConfigurer.isInitialized()).isFalse());
    }

    void verifyNetworkConfigurerInitialized(NetworkConfigurer networkConfigurer) {
        assertThat(networkConfigurer)
                .asInstanceOf(type(LazyNetworkConfigurer.class))
                .satisfies(lazyNetworkConfigurer -> assertThat(lazyNetworkConfigurer.isInitialized()).isTrue());
    }

}
