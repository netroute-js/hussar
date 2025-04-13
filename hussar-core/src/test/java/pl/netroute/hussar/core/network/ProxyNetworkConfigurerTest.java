package pl.netroute.hussar.core.network;

import eu.rekawek.toxiproxy.ToxiproxyClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import pl.netroute.hussar.core.api.Endpoint;
import pl.netroute.hussar.core.helper.SchemesHelper;
import pl.netroute.hussar.core.test.stub.Mock;

import java.util.List;

public class ProxyNetworkConfigurerTest {
    private ProxyNetworkConfigurer configurer;
    private ProxyNetworkConfigurerVerifier verifier;
    
    @BeforeEach
    public void setup() {
        var proxyClient = Mock.defaultMock(ToxiproxyClient.class);

        configurer = new ProxyNetworkConfigurer(proxyClient);
        verifier = new ProxyNetworkConfigurerVerifier(proxyClient);
    }

    @ParameterizedTest
    @MethodSource("endpointVariants")
    public void shouldConfigureNetworkForSingleEndpoint(List<Endpoint> endpoints) {
        // given
        var networkPrefix = "net";

        // when
        var network = configurer.configure(networkPrefix, endpoints);

        // then
        verifier.verifyNetworkConfigured(network, networkPrefix, endpoints);
    }

    private static List<Arguments> endpointVariants() {
        var localhostEndpoint = Endpoint.of(SchemesHelper.HTTP_SCHEME, "localhost", 8080);
        var devEndpoint = Endpoint.of(SchemesHelper.HTTP_SCHEME, "dev.host", 9080);
        var schemeLessEndpoint = Endpoint.schemeLess("host.com", 7080);

        return List.of(
                Arguments.of(List.of(localhostEndpoint)),
                Arguments.of(List.of(devEndpoint)),
                Arguments.of(List.of(schemeLessEndpoint)),
                Arguments.of(List.of(localhostEndpoint, devEndpoint, schemeLessEndpoint))
        );
    }

}