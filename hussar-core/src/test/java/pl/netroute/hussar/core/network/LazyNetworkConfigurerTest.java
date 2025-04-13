package pl.netroute.hussar.core.network;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import pl.netroute.hussar.core.api.Endpoint;
import pl.netroute.hussar.core.network.api.NetworkConfigurer;
import pl.netroute.hussar.core.test.factory.EndpointTestFactory;
import pl.netroute.hussar.core.test.stub.Mock;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class LazyNetworkConfigurerTest {
    private LazyNetworkConfigurer configurer;
    private LazyNetworkConfigurerVerifier verifier;

    @BeforeEach
    public void setup() {
        configurer = new LazyNetworkConfigurer();
    }

    @ParameterizedTest
    @MethodSource("endpointVariants")
    public void shouldConfigureNetwork(List<Endpoint> endpoints) {
        // given
        var networkPrefix = "net";
        var delegateConfigurer = Mock.defaultMock(NetworkConfigurer.class);

        configurer.setNetworkConfigurer(delegateConfigurer);

        // when
        var network = configurer.configure(networkPrefix, endpoints);

        // then
        verifier = new LazyNetworkConfigurerVerifier(delegateConfigurer);
        verifier.verifyNetworkConfigured(network, networkPrefix, endpoints);
    }

    @ParameterizedTest
    @MethodSource("endpointVariants")
    public void shouldFailConfiguringNetworkWhenNotInitialized(List<Endpoint> endpoints) {
        // given
        var networkPrefix = "net";

        // when
        // then
        assertThatThrownBy(() -> configurer.configure(networkPrefix, endpoints))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Expected NetworkConfigurer to be initialized");
    }

    private static List<Arguments> endpointVariants() {
        var endpointA = EndpointTestFactory.createHttp();
        var endpointB = EndpointTestFactory.createSchemeLess();

        return List.of(
                Arguments.of(List.of(endpointA)),
                Arguments.of(List.of(endpointA, endpointB))
        );
    }
}