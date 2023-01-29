package pl.netroute.hussar.spring.boot;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.netroute.hussar.core.Endpoint;
import pl.netroute.hussar.spring.boot.client.SimpleApplicationClient;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class SpringApplicationTest {
    private static final int PORT_RANGE_START = 30000;
    private static final int PORT_RANGE_END = 40000;

    private static final String LOCALHOST = "localhost";
    private static final String ENDPOINT_REGEX = "http://localhost:\\d+";

    private static final String PING_RESPONSE = "pong";

    private SpringApplication application;

    @BeforeEach
    public void setup() {
        application = SpringApplication.newApplication(SimpleSpringApplication.class);
    }

    @Test
    public void shouldStartApplication() {
        // given
        // when
        application.start();

        // then
        var initialized = application.isInitialized();
        var endpoints = application.getEndpoints();

        assertInitialized(initialized);
        assertEndpointExists(endpoints);
        assertPingEndpointAccessible();
    }

    @Test
    public void shouldSkipStartingApplicationWhenAlreadyStarted() {
        // given
        // when
        application.start();
        application.start();

        // then
        var initialized = application.isInitialized();
        var endpoints = application.getEndpoints();

        assertInitialized(initialized);
        assertEndpointExists(endpoints);
        assertPingEndpointAccessible();
    }

    @Test
    public void shouldShutdownApplication() {
        // given
        application.start();

        // when
        application.shutdown();

        // then
        var initialized = application.isInitialized();
        var endpoints = application.getEndpoints();

        assertNotInitialized(initialized);
        assertNoEndpointExists(endpoints);
    }

    @Test
    public void shouldSkipShuttingDownApplicationWhenNotStarted() {
        // given
        // when
        // then
        application.shutdown();
    }

    @Test
    public void shouldReturnNotInitializedWhenApplicationNotStarted() {
        // given
        // when
        var initialized = application.isInitialized();

        // then
        assertNotInitialized(initialized);
    }

    @Test
    public void shouldReturnNoEndpointsWhenApplicationNotStarted() {
        // given
        // when
        var endpoints = application.getEndpoints();

        // then
        assertNoEndpointExists(endpoints);
    }

    private void assertInitialized(boolean initialized) {
        assertThat(initialized).isTrue();
    }

    private void assertNotInitialized(boolean initialized) {
        assertThat(initialized).isFalse();
    }

    private void assertNoEndpointExists(List<Endpoint> endpoints) {
        assertThat(endpoints).isEmpty();
    }

    private void assertEndpointExists(List<Endpoint> endpoints) {
        assertThat(endpoints).hasSize(1);

        var endpoint = endpoints.get(0);
        assertThat(endpoint.getHost()).isEqualTo(LOCALHOST);
        assertThat(endpoint.getPort()).isBetween(PORT_RANGE_START, PORT_RANGE_END);
        assertThat(endpoint.getAddress()).matches(ENDPOINT_REGEX);
    }

    private void assertPingEndpointAccessible() {
        var endpoint = application
                .getEndpoints()
                .get(0);

        var pingResponse = new SimpleApplicationClient(endpoint).ping();
        assertThat(pingResponse).isEqualTo(PING_RESPONSE);
    }

}
