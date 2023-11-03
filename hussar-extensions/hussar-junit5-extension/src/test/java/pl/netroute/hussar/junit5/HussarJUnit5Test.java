package pl.netroute.hussar.junit5;

import com.netroute.hussar.service.wiremock.WiremockDockerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import pl.netroute.hussar.core.annotation.HussarApplication;
import pl.netroute.hussar.core.annotation.HussarEnvironment;
import pl.netroute.hussar.core.annotation.HussarService;
import pl.netroute.hussar.core.api.Application;
import pl.netroute.hussar.junit5.client.ClientFactory;
import pl.netroute.hussar.junit5.client.SimpleApplicationClient;
import pl.netroute.hussar.junit5.client.WiremockClient;
import pl.netroute.hussar.junit5.config.TestEnvironmentConfigurerProvider;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(HussarJUnit5Extension.class)
@HussarEnvironment(configurerProvider = TestEnvironmentConfigurerProvider.class)
public class HussarJUnit5Test {
    private static final String PING_RESPONSE = "pong";

    @HussarApplication
    Application application;

    @HussarService(name = TestEnvironmentConfigurerProvider.WIREMOCK_A)
    WiremockDockerService wiremockServiceA;

    @HussarService(name = TestEnvironmentConfigurerProvider.WIREMOCK_B)
    WiremockDockerService wiremockServiceB;

    @Test
    public void shouldStartupEnvironment() {
        // given
        var wiremockEndpointA = wiremockServiceA.getEndpoints().get(0);
        var wiremockEndpointB = wiremockServiceB.getEndpoints().get(0);

        var wiremockClientA = new WiremockClient(wiremockEndpointA.getAddress());
        var wiremockClientB = new WiremockClient(wiremockEndpointB.getAddress());

        var applicationClient = applicationClient(application);

        // when
        // then
        assertWiremockReachable(wiremockClientA);
        assertWiremockReachable(wiremockClientB);

        assertPingEndpointAccessible(applicationClient);
        assertPropertyConfigured(applicationClient, TestEnvironmentConfigurerProvider.PROPERTY_A, TestEnvironmentConfigurerProvider.PROPERTY_A_VALUE);
        assertPropertyConfigured(applicationClient, TestEnvironmentConfigurerProvider.PROPERTY_B, TestEnvironmentConfigurerProvider.PROPERTY_B_VALUE);
        assertPropertyConfigured(applicationClient, TestEnvironmentConfigurerProvider.SUB_PROPERTY_A, TestEnvironmentConfigurerProvider.ENV_VARIABLE_A_VALUE);
        assertPropertyConfigured(applicationClient, TestEnvironmentConfigurerProvider.WIREMOCK_INSTANCE_A_URL_PROPERTY, wiremockEndpointA.getAddress());
        assertPropertyConfigured(applicationClient, TestEnvironmentConfigurerProvider.WIREMOCK_INSTANCE_B_URL_PROPERTY, wiremockEndpointB.getAddress());
    }

    private void assertWiremockReachable(WiremockClient wiremock) {
        assertThat(wiremock.isReachable()).isTrue();
    }

    private void assertPropertyConfigured(SimpleApplicationClient client, String property, String expectedValue) {
        var foundProperty = client.getProperty(property);

        assertThat(foundProperty).hasValue(expectedValue);
    }

    private void assertPingEndpointAccessible(SimpleApplicationClient client) {
        var pingResponse = client.ping();

        assertThat(pingResponse).isEqualTo(PING_RESPONSE);
    }

    private SimpleApplicationClient applicationClient(Application application) {
        var endpoint = application
                .getEndpoints()
                .get(0);

        return ClientFactory.create(endpoint, SimpleApplicationClient.class);
    }
}
