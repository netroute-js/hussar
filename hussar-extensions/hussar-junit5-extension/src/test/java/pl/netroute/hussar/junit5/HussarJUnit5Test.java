package pl.netroute.hussar.junit5;

import com.netroute.hussar.wiremock.WiremockService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import pl.netroute.hussar.core.annotation.HussarEnvironment;
import pl.netroute.hussar.core.annotation.HussarService;
import pl.netroute.hussar.core.api.Service;
import pl.netroute.hussar.junit5.client.WiremockClient;
import pl.netroute.hussar.junit5.config.TestEnvironmentConfigurerProvider;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(HussarJUnit5Extension.class)
@HussarEnvironment(configurerProvider = TestEnvironmentConfigurerProvider.class)
public class HussarJUnit5Test {

    @HussarService(name = TestEnvironmentConfigurerProvider.WIREMOCK_A)
    WiremockService wiremockServiceA;

    @HussarService(name = TestEnvironmentConfigurerProvider.WIREMOCK_B)
    WiremockService wiremockServiceB;

    @Test
    public void shouldStartupEnvironment() {
        // given
        var wiremockEndpointA = wiremockServiceA.getEndpoints().get(0);
        var wiremockEndpointB = wiremockServiceB.getEndpoints().get(0);

        var wiremockClientA = new WiremockClient(wiremockEndpointA.getAddress());
        var wiremockClientB = new WiremockClient(wiremockEndpointB.getAddress());

        // when
        // then
        assertWiremockReachable(wiremockClientA);
        assertWiremockReachable(wiremockClientB);
    }

    private void assertWiremockReachable(WiremockClient wiremock) {
        assertThat(wiremock.isReachable()).isTrue();
    }

}
