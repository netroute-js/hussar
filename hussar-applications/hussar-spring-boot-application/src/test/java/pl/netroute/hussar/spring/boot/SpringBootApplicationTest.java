package pl.netroute.hussar.spring.boot;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.netroute.hussar.core.api.application.ApplicationStartupContext;
import pl.netroute.hussar.core.api.configuration.ConfigurationEntry;
import pl.netroute.hussar.core.api.Endpoint;
import pl.netroute.hussar.spring.boot.client.ClientFactory;
import pl.netroute.hussar.spring.boot.client.SimpleApplicationClient;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.netroute.hussar.core.api.configuration.ConfigurationEntry.envVariable;
import static pl.netroute.hussar.core.api.configuration.ConfigurationEntry.property;

public class SpringBootApplicationTest {
    private static final int PORT_RANGE_START = 30000;
    private static final int PORT_RANGE_END = 40000;

    private static final int INITIAL_VERSION = 1;

    private static final String LOCALHOST = "localhost";
    private static final String ENDPOINT_REGEX = "http://localhost:\\d+";

    private static final String PING_RESPONSE = "pong";

    private static final String SERVER_NAME_PROPERTY = "server.name";
    private static final String SERVER_AUTH_PROPERTY = "server.auth";
    private static final String SERVER_NAME_PROPERTY_VALUE = "husar-junit5";
    private static final String SERVER_AUTH_PROPERTY_VALUE = "credentials";

    private static final String APPLICATION_WIREMOCK_URL_PROPERTY = "application.wiremock.url";
    private static final String APPLICATION_WIREMOCK_URL_PROPERTY_VALUE = "https://hussar.wiremock.dev";

    private static final String METRICS_URL_PROPERTY = "metrics.url";
    private static final String METRICS_URL_ENV_VARIABLE = "METRICS_URL";
    private static final String METRICS_URL_ENV_VARIABLE_VALUE = "https://hussar.dev/metrics";

    private static final String MANAGEMENT_TRACING_ENABLED_PROPERTY = "management.tracing.enabled";
    private static final String MANAGEMENT_TRACING_ENABLED_PROPERTY_VALUE = "false";

    private SpringBootApplication application;

    @BeforeEach
    public void setup() {
        application = SpringBootApplication.newApplication(SimpleSpringApplication.class);
    }

    @Test
    public void shouldStartApplication() {
        // given
        var serverAuthProperty = property(SERVER_AUTH_PROPERTY, SERVER_AUTH_PROPERTY_VALUE);
        var metricsUrlEnvVariable = envVariable(METRICS_URL_ENV_VARIABLE, METRICS_URL_ENV_VARIABLE_VALUE);
        var externalConfigurations = Set.<ConfigurationEntry>of(
                serverAuthProperty,
                metricsUrlEnvVariable
        );

        var startupContext = new ApplicationStartupContext(externalConfigurations);

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
    }

    @Test
    public void shouldSkipStartingApplicationWhenAlreadyStarted() {
        // given
        var serverAuthProperty = property(SERVER_AUTH_PROPERTY, SERVER_AUTH_PROPERTY_VALUE);
        var metricsUrlEnvVariable = envVariable(METRICS_URL_ENV_VARIABLE, METRICS_URL_ENV_VARIABLE_VALUE);
        var externalConfigurations = Set.<ConfigurationEntry>of(
                serverAuthProperty,
                metricsUrlEnvVariable
        );

        var startupContext = new ApplicationStartupContext(externalConfigurations);

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
    }

    @Test
    public void shouldRestartApplication() {
        // given
        var serverAuthProperty = property(SERVER_AUTH_PROPERTY, SERVER_AUTH_PROPERTY_VALUE);
        var metricsUrlEnvVariable = envVariable(METRICS_URL_ENV_VARIABLE, METRICS_URL_ENV_VARIABLE_VALUE);
        var externalConfigurations = Set.<ConfigurationEntry>of(
                serverAuthProperty,
                metricsUrlEnvVariable
        );

        var startupContext = new ApplicationStartupContext(externalConfigurations);

        // when
        application.start(startupContext);

        var endpoints = application.getEndpoints();
        var client = applicationClient(endpoints);

        var incrementedVersion = client.incrementVersion();

        application.restart();

        // then
        var expectedVersion = 2;
        assertVersionChanged(incrementedVersion, expectedVersion);

        var initialized = application.isInitialized();
        assertInitialized(initialized);

        endpoints = application.getEndpoints();
        client = applicationClient(endpoints);

        assertPingEndpointAccessible(client);
        assertConfiguredProperties(client);
        assertVersionReset(client);
    }

    @Test
    public void shouldShutdownApplication() {
        // given
        var startupContext = new ApplicationStartupContext(Set.of());

        // when
        application.start(startupContext);
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

        var endpoint = endpoints.getFirst();
        assertThat(endpoint.host()).isEqualTo(LOCALHOST);
        assertThat(endpoint.port()).isBetween(PORT_RANGE_START, PORT_RANGE_END);
        assertThat(endpoint.address()).matches(ENDPOINT_REGEX);
    }

    private void assertPingEndpointAccessible(SimpleApplicationClient client) {
        var pingResponse = client.ping();

        assertThat(pingResponse).isEqualTo(PING_RESPONSE);
    }

    private void assertVersionChanged(int version, int expectedVersion) {
        assertThat(version).isEqualTo(expectedVersion);
    }

    private void assertVersionReset(SimpleApplicationClient client) {
        var version = client.getVersion();

        assertThat(version).isEqualTo(INITIAL_VERSION);
    }

    private void assertConfiguredProperties(SimpleApplicationClient client) {
        assertPropertyConfigured(client, DynamicConfigurationConfigurer.SERVER_PORT);
        assertPropertyConfigured(client, SERVER_NAME_PROPERTY, SERVER_NAME_PROPERTY_VALUE);
        assertPropertyConfigured(client, SERVER_AUTH_PROPERTY, SERVER_AUTH_PROPERTY_VALUE);
        assertPropertyConfigured(client, MANAGEMENT_TRACING_ENABLED_PROPERTY, MANAGEMENT_TRACING_ENABLED_PROPERTY_VALUE);
        assertPropertyConfigured(client, METRICS_URL_PROPERTY, METRICS_URL_ENV_VARIABLE_VALUE);
        assertPropertyConfigured(client, APPLICATION_WIREMOCK_URL_PROPERTY, APPLICATION_WIREMOCK_URL_PROPERTY_VALUE);
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
        var endpoint = endpoints.getFirst();

        return ClientFactory.create(endpoint, SimpleApplicationClient.class);
    }

}
