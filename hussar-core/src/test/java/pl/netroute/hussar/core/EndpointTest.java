package pl.netroute.hussar.core;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class EndpointTest {

    @Test
    public void shouldCreateEndpointWithoutScheme() {
        // given
        var host = "some-host";
        var port = 50000;

        // when
        var endpoint = Endpoint.schemeLess(host, port);

        // then
        var expectedAddress = String.format("%s:%d", host, port);

        assertThat(endpoint.host()).isEqualTo(host);
        assertThat(endpoint.port()).isEqualTo(port);
        assertThat(endpoint.address()).isEqualTo(expectedAddress);
    }

    @Test
    public void shouldCreateEndpointWithScheme() {
        // given
        var scheme = "http://";
        var host = "some-host";
        var port = 50000;

        // when
        var endpoint = Endpoint.of(scheme, host, port);

        // then
        var expectedAddress = String.format("%s%s:%d", scheme, host, port);

        assertThat(endpoint.host()).isEqualTo(host);
        assertThat(endpoint.port()).isEqualTo(port);
        assertThat(endpoint.address()).isEqualTo(expectedAddress);
    }

}
