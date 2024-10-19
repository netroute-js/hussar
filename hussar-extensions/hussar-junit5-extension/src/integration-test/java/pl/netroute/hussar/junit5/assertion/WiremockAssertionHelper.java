package pl.netroute.hussar.junit5.assertion;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import pl.netroute.hussar.core.api.Endpoint;
import pl.netroute.hussar.core.helper.EndpointHelper;
import pl.netroute.hussar.junit5.client.SimpleApplicationClient;
import pl.netroute.hussar.junit5.client.WiremockClient;
import pl.netroute.hussar.service.wiremock.WiremockDockerService;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.netroute.hussar.junit5.assertion.ApplicationPropertiesAssertionHelper.assertPropertyConfigured;
import static pl.netroute.hussar.junit5.config.ApplicationProperties.WIREMOCK_ALTERNATIVE_URL_PROPERTY;
import static pl.netroute.hussar.junit5.config.ApplicationProperties.WIREMOCK_URL_PROPERTY;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class WiremockAssertionHelper {

    public static void assertWiremockBootstrapped(@NonNull WiremockDockerService wiremockService,
                                                  @NonNull SimpleApplicationClient applicationClient) {
        var endpoint = EndpointHelper.getAnyEndpointOrFail(wiremockService);

        assertWiremockReachable(endpoint);
        assertPropertyConfigured(WIREMOCK_URL_PROPERTY, endpoint.address(), applicationClient);
        assertPropertyConfigured(WIREMOCK_ALTERNATIVE_URL_PROPERTY, endpoint.address(), applicationClient);
    }

    private static void assertWiremockReachable(Endpoint endpoint) {
        var wiremockClient = new WiremockClient(endpoint.address());

        assertThat(wiremockClient.isReachable()).isTrue();
    }
}