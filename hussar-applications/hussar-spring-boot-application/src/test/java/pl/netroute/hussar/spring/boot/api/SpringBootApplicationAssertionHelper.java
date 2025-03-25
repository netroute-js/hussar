package pl.netroute.hussar.spring.boot.api;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import pl.netroute.hussar.core.application.api.Application;
import pl.netroute.hussar.spring.boot.api.client.SimpleApplicationClient;

import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SpringBootApplicationAssertionHelper {
    private static final int INITIAL_VERSION = 1;

    private static final String ENDPOINT_REGEX = "http://localhost:\\d+";

    private static final String PING_RESPONSE = "pong";

    public static final String SERVER_NAME_PROPERTY = "server.name";
    public static final String SERVER_AUTH_PROPERTY = "server.auth";
    public static final String SERVER_NAME_PROPERTY_VALUE = "husar-junit5";
    public static final String SERVER_AUTH_PROPERTY_VALUE = "credentials";

    public static final String APPLICATION_WIREMOCK_URL_PROPERTY = "application.wiremock.url";
    public static final String APPLICATION_WIREMOCK_URL_PROPERTY_VALUE = "https://hussar.wiremock.dev";

    public static final String METRICS_URL_PROPERTY = "metrics.url";
    public static final String METRICS_URL_ENV_VARIABLE = "METRICS_URL";
    public static final String METRICS_URL_ENV_VARIABLE_VALUE = "https://hussar.dev/metrics";

    public static final String MANAGEMENT_TRACING_ENABLED_PROPERTY = "management.tracing.enabled";
    public static final String MANAGEMENT_TRACING_ENABLED_PROPERTY_VALUE = "false";

    public static void assertApplicationInitialized(@NonNull Application application) {
        var initialized = application.isInitialized();

        assertThat(initialized).isTrue();
    }

    public static void assertApplicationNotInitialized(@NonNull Application application) {
        var initialized = application.isInitialized();

        assertThat(initialized).isFalse();
    }

    public static void assertApplicationSingleEndpointExists(@NonNull Application application) {
        var endpoints = application.getEndpoints();
        assertThat(endpoints).hasSize(1);

        var endpoint = endpoints.getFirst();
        assertThat(endpoint.address()).matches(ENDPOINT_REGEX);
    }

    public static void assertApplicationMultipleEndpointsExists(@NonNull Application application, int numberOfEndpoints) {
        var endpoints = application.getEndpoints();
        assertThat(endpoints).hasSize(numberOfEndpoints);

        endpoints.forEach(endpoint -> assertThat(endpoint.address()).matches(ENDPOINT_REGEX));
    }

    public static void assertNoApplicationEndpointsExist(@NonNull Application application) {
        var endpoints = application.getEndpoints();
        assertThat(endpoints).isEmpty();
    }

    public static void assertApplicationPingEndpointAccessible(@NonNull Application application) {
        runOnApplicationClient(application, client -> assertThat(client.ping()).isEqualTo(PING_RESPONSE));
    }

    public static void assertApplicationPropertiesConfigured(@NonNull Application application) {
        runOnApplicationClient(application, client -> {
            assertApplicationPropertyConfigured(client, DynamicConfigurationConfigurer.SERVER_PORT);
            assertApplicationPropertyConfigured(client, SERVER_NAME_PROPERTY, SERVER_NAME_PROPERTY_VALUE);
            assertApplicationPropertyConfigured(client, SERVER_AUTH_PROPERTY, SERVER_AUTH_PROPERTY_VALUE);
            assertApplicationPropertyConfigured(client, MANAGEMENT_TRACING_ENABLED_PROPERTY, MANAGEMENT_TRACING_ENABLED_PROPERTY_VALUE);
            assertApplicationPropertyConfigured(client, METRICS_URL_PROPERTY, METRICS_URL_ENV_VARIABLE_VALUE);
            assertApplicationPropertyConfigured(client, APPLICATION_WIREMOCK_URL_PROPERTY, APPLICATION_WIREMOCK_URL_PROPERTY_VALUE);
        });
    }

    public static void assertApplicationRestarted(@NonNull Application application) {
        runOnApplicationClient(application, client -> assertThat(client.getVersion()).isEqualTo(INITIAL_VERSION));
    }

    public static void assertApplicationDependencyInjector(@NonNull Application application) {
        var dependencyInjector = application.getDependencyInjector();

        assertThat(dependencyInjector).isNotNull();
        assertThat(dependencyInjector).isInstanceOf(SpringBootDependencyInjector.class);
    }

    public static void assertNoApplicationDependencyInjector(@NonNull Application application) {
        var dependencyInjector = application.getDependencyInjector();

        assertThat(dependencyInjector).isNull();
    }

    private static void runOnApplicationClient(Application application, Consumer<SimpleApplicationClient> onApplicationClient) {
        application
                .getEndpoints()
                .stream()
                .map(SimpleApplicationClient::newClient)
                .forEach(onApplicationClient);
    }

    private static void assertApplicationPropertyConfigured(SimpleApplicationClient client, String property, String expectedValue) {
        var foundProperty = client.getProperty(property);

        assertThat(foundProperty).hasValue(expectedValue);
    }

    private static void assertApplicationPropertyConfigured(SimpleApplicationClient client, String property) {
        var foundProperty = client.getProperty(property);

        assertThat(foundProperty).isPresent();
    }

}
