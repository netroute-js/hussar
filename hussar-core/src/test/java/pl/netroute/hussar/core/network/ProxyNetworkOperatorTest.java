package pl.netroute.hussar.core.network;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.ToxiproxyContainer;
import pl.netroute.hussar.core.test.stub.Mock;

public class ProxyNetworkOperatorTest {
    private ProxyNetworkOperator networkOperator;
    private ProxyNetworkOperatorVerifier verifier;

    @BeforeEach
    public void setup() {
        var proxyContainer = Mock.defaultMock(ToxiproxyContainer.class);

        networkOperator = new ProxyNetworkOperator(proxyContainer);
        verifier = new ProxyNetworkOperatorVerifier(proxyContainer);
    }

    @Test
    public void shouldStartNetworkOperator() {
        // given
        var context = NetworkOperatorStartupContext.defaultContext();

        // when
        networkOperator.start(context);

        // then
        verifier.verifyNetworkOperatorStarted();
    }

    @Test
    public void shutShutdownNetworkOperator() {
        // given
        // when
        networkOperator.shutdown();

        // then
        verifier.verifyNetworkOperatorStopped();
    }

    @Test
    public void shouldReturnInitializedNetworkConfigurerWhenNetworkOperatorStarted() {
        // given
        var context = NetworkOperatorStartupContext.defaultContext();

        // when
        networkOperator.start(context);

        var networkConfigurer = networkOperator.getNetworkConfigurer();

        // then
        verifier.verifyNetworkConfigurerInitialized(networkConfigurer);
    }

    @Test
    public void shouldReturnNotInitializedNetworkConfigurerWhenNetworkOperatorNotStarted() {
        // given
        // when
        var networkConfigurer = networkOperator.getNetworkConfigurer();

        // then
        verifier.verifyNetworkConfigurerNotInitialized(networkConfigurer);
    }

}
