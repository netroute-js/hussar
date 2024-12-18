package pl.netroute.hussar.junit5.assertion;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import pl.netroute.hussar.core.api.application.Application;
import pl.netroute.hussar.junit5.helper.ApplicationClientRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.netroute.hussar.junit5.assertion.ApplicationPropertiesAssertionHelper.assertPropertyConfigured;
import static pl.netroute.hussar.junit5.config.ApplicationProperties.METRICS_URL_PROPERTY;
import static pl.netroute.hussar.junit5.config.ApplicationProperties.METRICS_URL_PROPERTY_VALUE;
import static pl.netroute.hussar.junit5.config.ApplicationProperties.SERVER_AUTH_PROPERTY;
import static pl.netroute.hussar.junit5.config.ApplicationProperties.SERVER_AUTH_PROPERTY_VALUE;
import static pl.netroute.hussar.junit5.config.ApplicationProperties.SERVER_NAME_PROPERTY;
import static pl.netroute.hussar.junit5.config.ApplicationProperties.SERVER_NAME_PROPERTY_VALUE;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ApplicationAssertionHelper {
    private static final String PING_RESPONSE = "pong";

    public static void assertApplicationBootstrapped(@NonNull Application application) {
        var applicationClientRunner = new ApplicationClientRunner(application);

        assertApplicationInitialized(application);
        assertPingEndpointAccessible(application);
        applicationClientRunner.run(applicationClient -> assertPropertyConfigured(SERVER_NAME_PROPERTY, SERVER_NAME_PROPERTY_VALUE, applicationClient));
        applicationClientRunner.run(applicationClient -> assertPropertyConfigured(SERVER_AUTH_PROPERTY, SERVER_AUTH_PROPERTY_VALUE, applicationClient));
        applicationClientRunner.run(applicationClient -> assertPropertyConfigured(METRICS_URL_PROPERTY, METRICS_URL_PROPERTY_VALUE, applicationClient));
    }

    private static void assertApplicationInitialized(@NonNull Application application) {
        assertThat(application.isInitialized()).isTrue();
    }

    private static void assertPingEndpointAccessible(@NonNull Application application) {
        var applicationClientRunner = new ApplicationClientRunner(application);

        applicationClientRunner.run(client -> assertThat(client.ping()).isEqualTo(PING_RESPONSE));
    }

}
