package pl.netroute.hussar.spring.boot;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.netroute.hussar.core.application.api.ApplicationStartupContext;
import pl.netroute.hussar.core.configuration.api.ConfigurationEntry;
import pl.netroute.hussar.spring.boot.client.SimpleApplicationClient;

import java.util.Set;

import static pl.netroute.hussar.core.configuration.api.ConfigurationEntry.envVariable;
import static pl.netroute.hussar.core.configuration.api.ConfigurationEntry.property;
import static pl.netroute.hussar.spring.boot.SpringBootApplicationAssertionHelper.METRICS_URL_ENV_VARIABLE;
import static pl.netroute.hussar.spring.boot.SpringBootApplicationAssertionHelper.METRICS_URL_ENV_VARIABLE_VALUE;
import static pl.netroute.hussar.spring.boot.SpringBootApplicationAssertionHelper.SERVER_AUTH_PROPERTY;
import static pl.netroute.hussar.spring.boot.SpringBootApplicationAssertionHelper.SERVER_AUTH_PROPERTY_VALUE;
import static pl.netroute.hussar.spring.boot.SpringBootApplicationAssertionHelper.assertApplicationInitialized;
import static pl.netroute.hussar.spring.boot.SpringBootApplicationAssertionHelper.assertApplicationNotInitialized;
import static pl.netroute.hussar.spring.boot.SpringBootApplicationAssertionHelper.assertApplicationPingEndpointAccessible;
import static pl.netroute.hussar.spring.boot.SpringBootApplicationAssertionHelper.assertApplicationPropertiesConfigured;
import static pl.netroute.hussar.spring.boot.SpringBootApplicationAssertionHelper.assertApplicationRestarted;
import static pl.netroute.hussar.spring.boot.SpringBootApplicationAssertionHelper.assertApplicationSingleEndpointExists;
import static pl.netroute.hussar.spring.boot.SpringBootApplicationAssertionHelper.assertNoApplicationEndpointsExist;

public class SpringBootApplicationTest {
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
        assertApplicationInitialized(application);
        assertApplicationSingleEndpointExists(application);
        assertApplicationPingEndpointAccessible(application);
        assertApplicationPropertiesConfigured(application);
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
        assertApplicationInitialized(application);
        assertApplicationSingleEndpointExists(application);
        assertApplicationPingEndpointAccessible(application);
        assertApplicationPropertiesConfigured(application);
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

        var endpoint = application
                .getEndpoints()
                .getFirst();

        SimpleApplicationClient
                .newClient(endpoint)
                .incrementVersion();

        application.restart();

        // then
        assertApplicationInitialized(application);
        assertApplicationSingleEndpointExists(application);
        assertApplicationPingEndpointAccessible(application);
        assertApplicationPropertiesConfigured(application);
        assertApplicationRestarted(application);
    }

    @Test
    public void shouldShutdownApplication() {
        // given
        var startupContext = new ApplicationStartupContext(Set.of());

        // when
        application.start(startupContext);
        application.shutdown();

        // then
        assertApplicationNotInitialized(application);
        assertNoApplicationEndpointsExist(application);
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
        // then
        assertApplicationNotInitialized(application);
    }

    @Test
    public void shouldReturnNoEndpointsWhenApplicationNotStarted() {
        // given
        // when
        // then
        assertNoApplicationEndpointsExist(application);
    }

}
