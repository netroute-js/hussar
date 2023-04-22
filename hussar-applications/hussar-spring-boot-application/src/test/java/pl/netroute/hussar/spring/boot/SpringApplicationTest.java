package pl.netroute.hussar.spring.boot;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.netroute.hussar.core.Endpoint;
import pl.netroute.hussar.core.api.ApplicationStartupContext;
import pl.netroute.hussar.spring.boot.client.ClientFactory;
import pl.netroute.hussar.spring.boot.client.SimpleApplicationClient;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class SpringApplicationTest {
    private static final int PORT_RANGE_START = 30000;
    private static final int PORT_RANGE_END = 40000;

    private static final String LOCALHOST = "localhost";
    private static final String ENDPOINT_REGEX = "http://localhost:\\d+";

    private static final String PING_RESPONSE = "pong";

    private static final String MY_PROPERTY_A = "my.propertyA";
    private static final String MY_PROPERTY_B = "my.propertyB";

    private static final String MY_PROPERTY_VALUE_A = "some-valueA";
    private static final String MY_PROPERTY_VALUE_B = "some-valueB";

    private SpringApplication application;

    @BeforeEach
    public void setup() {
        application = SpringApplication.newApplication(SimpleSpringApplication.class);
    }

    @Test
    public void shouldStartApplication() {
        // given
        var startupContext = new ApplicationStartupContext(Map.of(MY_PROPERTY_B, MY_PROPERTY_VALUE_B));

        // when
        application.start(startupContext);

        // then
        var initialized = application.isInitialized();
        assertInitialized(initialized);

        var endpoints = application.getEndpoints();
        assertEndpointExists(endpoints);

        var client = applicationClient(endpoints);
        assertPingEndpointAccessible(client);
        assertConfiguredProperties(client);
        assertNotConfiguredProperties(client);
    }

    @Test
    public void shouldSkipStartingApplicationWhenAlreadyStarted() {
        // given
        var startupContext = new ApplicationStartupContext(Map.of(MY_PROPERTY_B, MY_PROPERTY_VALUE_B));

        // when
        application.start(startupContext);
        application.start(startupContext);

        // then
        var initialized = application.isInitialized();
        assertInitialized(initialized);

        var endpoints = application.getEndpoints();
        assertEndpointExists(endpoints);

        var client = applicationClient(endpoints);
        assertPingEndpointAccessible(client);
        assertConfiguredProperties(client);
        assertNotConfiguredProperties(client);
    }

    @Test
    public void shouldShutdownApplication() {
        // given
        var startupContext = new ApplicationStartupContext(Map.of());

        application.start(startupContext);

        // when
        application.shutdown();

        // then
        var initialized = application.isInitialized();
        assertNotInitialized(initialized);

        var endpoints = application.getEndpoints();
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

    private void assertPingEndpointAccessible(SimpleApplicationClient client) {
        var pingResponse = client.ping();

        assertThat(pingResponse).isEqualTo(PING_RESPONSE);
    }

    private void assertConfiguredProperties(SimpleApplicationClient client) {
        assertPropertyConfigured(client, SpringProperties.SERVER_PORT);
        assertPropertyConfigured(client, MY_PROPERTY_B, MY_PROPERTY_VALUE_B);
    }

    private void assertNotConfiguredProperties(SimpleApplicationClient client) {
        assertPropertyNotConfigured(client, MY_PROPERTY_A);
    }

    private void assertPropertyNotConfigured(SimpleApplicationClient client, String property) {
        var foundProperty = client.getProperty(property);

        assertThat(foundProperty).isEmpty();
    }

    private void assertPropertyConfigured(SimpleApplicationClient client, String property, String expectedValue) {
        var foundProperty = client.getProperty(property);

        assertThat(foundProperty).hasValue(expectedValue);
    }

    private void assertPropertyConfigured(SimpleApplicationClient client, String property) {
        var foundProperty = client.getProperty(property);

        assertThat(foundProperty).isPresent();
    }

    private SimpleApplicationClient applicationClient(List<Endpoint> endpoints) {
        var endpoint = endpoints.get(0);

        return ClientFactory.create(endpoint, SimpleApplicationClient.class);
    }
}