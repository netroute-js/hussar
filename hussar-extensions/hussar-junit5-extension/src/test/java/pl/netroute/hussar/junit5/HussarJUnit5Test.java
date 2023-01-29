package pl.netroute.hussar.junit5;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import pl.netroute.hussar.core.annotation.HussarEnvironment;
import pl.netroute.hussar.core.helper.PropertiesHelper;
import pl.netroute.hussar.junit5.client.WiremockClient;
import pl.netroute.hussar.junit5.config.TestEnvironmentConfigurerProvider;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.netroute.hussar.junit5.config.TestEnvironmentConfigurerProvider.WIREMOCK_INSTANCE_A_URL_PROPERTY;
import static pl.netroute.hussar.junit5.config.TestEnvironmentConfigurerProvider.WIREMOCK_INSTANCE_B_URL_PROPERTY;

@ExtendWith(HussarJUnit5Extension.class)
@HussarEnvironment(configurerProvider = TestEnvironmentConfigurerProvider.class)
public class HussarJUnit5Test {

    @Test
    public void shouldStartupEnvironment() {
        // given
        var wiremockUrlA = PropertiesHelper.getPropertyOrFail(WIREMOCK_INSTANCE_A_URL_PROPERTY);
        var wiremockUrlB = PropertiesHelper.getPropertyOrFail(WIREMOCK_INSTANCE_B_URL_PROPERTY);

        var wiremockClientA = new WiremockClient(wiremockUrlA);
        var wiremockClientB = new WiremockClient(wiremockUrlB);

        // when
        var wiremockReachableA = wiremockClientA.isReachable();
        var wiremockReachableB = wiremockClientB.isReachable();

        // then
        assertThat(wiremockReachableA).isTrue();
        assertThat(wiremockReachableB).isTrue();
    }

}
