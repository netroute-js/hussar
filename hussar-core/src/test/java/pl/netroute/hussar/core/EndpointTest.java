package pl.netroute.hussar.core;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import pl.netroute.hussar.core.api.Endpoint;

import static org.assertj.core.api.Assertions.assertThat;

public class EndpointTest {
    private static final String ADDRESS_FORMAT = "%s%s:%d";
    private static final String SCHEME_LESS_ADDRESS_FORMAT = "%s:%d";

    @Test
    public void shouldCreateEndpointWithoutScheme() {
        // given
        var host = "some-host";
        var port = 50000;

        // when
        var endpoint = Endpoint.schemeLess(host, port);

        // then
        var expectedAddress = SCHEME_LESS_ADDRESS_FORMAT.formatted(host, port);

        assertThat(endpoint.host()).isEqualTo(host);
        assertThat(endpoint.port()).isEqualTo(port);
        assertThat(endpoint.hostPort()).isEqualTo(expectedAddress);
        assertThat(endpoint.address()).isEqualTo(expectedAddress);
    }

    @Test
    public void shouldCreateEndpointWithScheme() {
        // given
        var scheme = "http://";
        var host = "localhost";
        var port = 50000;

        // when
        var endpoint = Endpoint.of(scheme, host, port);

        // then
        var expectedHostPort = SCHEME_LESS_ADDRESS_FORMAT.formatted(host, port);
        var expectedAddress = ADDRESS_FORMAT.formatted(scheme, host, port);

        assertThat(endpoint.host()).isEqualTo(host);
        assertThat(endpoint.port()).isEqualTo(port);
        assertThat(endpoint.hostPort()).isEqualTo(expectedHostPort);
        assertThat(endpoint.address()).isEqualTo(expectedAddress);
    }

    @ParameterizedTest
    @ValueSource(strings = { "localhost", "127.0.0.1" })
    public void shouldReturnTrueWhenLocalhost(String host) {
        // given
        var port = 50000;

        // when
        var endpoint = Endpoint.schemeLess(host, port);

        // then
        assertThat(endpoint.isLocalhost()).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = { "some.host", "172.0.0.1" })
    public void shouldReturnFalseWhenNonLocalhost(String host) {
        // given
        var port = 50000;

        // when
        var endpoint = Endpoint.schemeLess(host, port);

        // then
        assertThat(endpoint.isLocalhost()).isFalse();
    }

}
