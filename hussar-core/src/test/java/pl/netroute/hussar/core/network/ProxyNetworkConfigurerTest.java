package pl.netroute.hussar.core.network;

import eu.rekawek.toxiproxy.ToxiproxyClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.testcontainers.containers.ToxiproxyContainer;
import pl.netroute.hussar.core.api.Endpoint;
import pl.netroute.hussar.core.docker.DockerHostResolver;
import pl.netroute.hussar.core.helper.SchemesHelper;
import pl.netroute.hussar.core.stub.helper.StubHelper;

import java.util.List;
import java.util.stream.IntStream;

import static org.mockito.Mockito.when;

public class ProxyNetworkConfigurerTest {
    private static final int PROXY_INITIAL_PORT = 8666;
    private static final int PROXY_MAPPED_INITIAL_PORT = 18666;

    private ToxiproxyContainer proxyContainer;
    private DockerHostResolver dockerHostResolver;

    private ProxyNetworkConfigurer configurer;
    private ProxyNetworkConfigurerVerifier verifier;
    
    @BeforeEach
    public void setup() {
        var proxyClient = StubHelper.defaultStub(ToxiproxyClient.class);
        proxyContainer = StubHelper.defaultStub(ToxiproxyContainer.class);
        dockerHostResolver = StubHelper.defaultStub(DockerHostResolver.class);

        configurer = new ProxyNetworkConfigurer(proxyContainer, proxyClient, dockerHostResolver);
        verifier = new ProxyNetworkConfigurerVerifier(proxyClient);
    }

    @ParameterizedTest
    @MethodSource("endpointVariants")
    public void shouldConfigureNetwork(List<Endpoint> endpoints) {
        // given
        var networkPrefix = "net";
        var dockerHost = "docker-host";
        var gatewayHost = "gateway-host";

        IntStream
                .range(0, endpoints.size())
                .forEach(index -> when(proxyContainer.getMappedPort(PROXY_INITIAL_PORT + index)).thenReturn(PROXY_MAPPED_INITIAL_PORT + index));

        when(dockerHostResolver.getHost()).thenReturn(dockerHost);
        when(dockerHostResolver.getGatewayHost()).thenReturn(gatewayHost);

        // when
        var network = configurer.configure(networkPrefix, endpoints);

        // then
        verifier.verifyNetworkConfigured(network, networkPrefix, gatewayHost, dockerHost, endpoints);
    }

    private static List<Arguments> endpointVariants() {
        var localhostEndpoint = Endpoint.of(SchemesHelper.HTTP_SCHEME, "localhost", 8080);
        var devEndpoint = Endpoint.of(SchemesHelper.HTTP_SCHEME, "dev.host", 9080);
        var schemeLessEndpoint = Endpoint.schemeLess("host.com", 7080);

        return List.of(
                Arguments.of(List.of(localhostEndpoint)),
                Arguments.of(List.of(devEndpoint)),
                Arguments.of(List.of(schemeLessEndpoint)),
                Arguments.of(List.of(localhostEndpoint, devEndpoint))
        );
    }

}