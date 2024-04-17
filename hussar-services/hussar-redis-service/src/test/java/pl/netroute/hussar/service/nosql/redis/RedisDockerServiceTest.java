package pl.netroute.hussar.service.nosql.redis;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.Container;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import pl.netroute.hussar.core.Endpoint;
import pl.netroute.hussar.core.api.ConfigurationEntry;
import pl.netroute.hussar.core.api.MapConfigurationRegistry;
import pl.netroute.hussar.core.api.ServiceStartupContext;
import pl.netroute.hussar.core.service.registerer.EndpointRegisterer;
import pl.netroute.hussar.service.nosql.redis.registerer.RedisCredentialsRegisterer;

import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static pl.netroute.hussar.core.service.assertion.ServiceAssertionHelper.assertEntriesRegistered;
import static pl.netroute.hussar.core.service.assertion.ServiceAssertionHelper.assertName;
import static pl.netroute.hussar.core.service.assertion.ServiceAssertionHelper.assertNoEntriesRegistered;
import static pl.netroute.hussar.core.service.assertion.ServiceAssertionHelper.assertSingleEndpoint;
import static pl.netroute.hussar.core.service.assertion.GenericContainerAssertionHelper.assertContainerCommandExecuted;
import static pl.netroute.hussar.core.service.assertion.GenericContainerAssertionHelper.assertContainerExposedPortConfigured;
import static pl.netroute.hussar.core.service.assertion.GenericContainerAssertionHelper.assertContainerLoggingConfigured;
import static pl.netroute.hussar.core.service.assertion.GenericContainerAssertionHelper.assertContainerNoEnvVariablesConfigured;
import static pl.netroute.hussar.core.service.assertion.GenericContainerAssertionHelper.assertContainerStarted;
import static pl.netroute.hussar.core.service.assertion.GenericContainerAssertionHelper.assertContainerStopped;
import static pl.netroute.hussar.core.service.assertion.GenericContainerAssertionHelper.assertContainerWaitStrategyConfigured;

public class RedisDockerServiceTest {
    private static final String REDIS_HOST = "localhost";
    private static final int REDIS_LISTENING_PORT = 6379;
    private static final int REDIS_MAPPED_PORT = 7000;

    private static final String REDIS_SERVICE_NAME = "redis-service";
    private static final String REDIS_SERVICE_IMAGE = "redis";

    private static final String REDIS_SCHEME = "redis://";

    private static final String REDIS_USERNAME = "default";
    private static final String REDIS_PASSWORD = "test";

    private static final String CONFIGURE_REDIS_PASSWORD_COMMAND = "redis-cli -h %s -p %d config set requirepass %s"
            .formatted(REDIS_HOST, REDIS_LISTENING_PORT, REDIS_PASSWORD);

    private static final int CONTAINER_COMMAND_FAILED_CODE = -1;
    private static final String CONTAINER_COMMAND_SPLITTER = " ";

    @Test
    public void shouldStartMinimalService() {
        // given
        var config = RedisDockerServiceConfig
                .builder()
                .name(REDIS_SERVICE_NAME)
                .dockerImage(REDIS_SERVICE_IMAGE)
                .scheme(REDIS_SCHEME)
                .registerEndpointUnderProperties(Set.of())
                .registerEndpointUnderEnvironmentVariables(Set.of())
                .registerUsernameUnderProperties(Set.of())
                .registerUsernameUnderEnvironmentVariables(Set.of())
                .registerPasswordUnderProperties(Set.of())
                .registerPasswordUnderEnvironmentVariables(Set.of())
                .build();

        var container = createStubContainer();
        var service = createRedisService(config, container);

        givenContainerAccessible(container);

        // when
        service.start(ServiceStartupContext.empty());

        // then
        var endpoint = Endpoint.of(REDIS_SCHEME, REDIS_HOST, REDIS_MAPPED_PORT);

        assertContainerStarted(container);
        assertContainerExposedPortConfigured(container, REDIS_LISTENING_PORT);
        assertContainerWaitStrategyConfigured(container, Wait.forListeningPort());
        assertContainerLoggingConfigured(container);
        assertContainerNoEnvVariablesConfigured(container);
        assertName(service, REDIS_SERVICE_NAME);
        assertSingleEndpoint(service, endpoint);
        assertNoEntriesRegistered(service);
    }

    @Test
    public void shouldStartExtendedService() {
        // given
        var endpointProperty = "endpoint.url";
        var endpointEnvVariable = "ENDPOINT_URL";

        var usernameProperty = "redis.username";
        var usernameEnvVariable = "REDIS_USERNAME";

        var passwordProperty = "redis.password";
        var passwordEnvVariable = "REDIS_PASSWORD";

        var config = RedisDockerServiceConfig
                .builder()
                .name(REDIS_SERVICE_NAME)
                .dockerImage(REDIS_SERVICE_IMAGE)
                .scheme(REDIS_SCHEME)
                .enablePassword(true)
                .registerEndpointUnderProperties(Set.of(endpointProperty))
                .registerEndpointUnderEnvironmentVariables(Set.of(endpointEnvVariable))
                .registerUsernameUnderProperties(Set.of(usernameProperty))
                .registerUsernameUnderEnvironmentVariables(Set.of(usernameEnvVariable))
                .registerPasswordUnderProperties(Set.of(passwordProperty))
                .registerPasswordUnderEnvironmentVariables(Set.of(passwordEnvVariable))
                .build();

        var container = createStubContainer();
        var service = createRedisService(config, container);

        givenContainerAccessible(container);

        // when
        service.start(ServiceStartupContext.empty());

        // then
        var endpoint = Endpoint.of(REDIS_SCHEME, REDIS_HOST, REDIS_MAPPED_PORT);
        var endpointPropertyEntry = ConfigurationEntry.property(endpointProperty, endpoint.address());
        var endpointEnvVariableEntry = ConfigurationEntry.envVariable(endpointEnvVariable, endpoint.address());

        var usernamePropertyEntry = ConfigurationEntry.property(usernameProperty, REDIS_USERNAME);
        var usernameEnvVariableEntry = ConfigurationEntry.envVariable(usernameEnvVariable, REDIS_USERNAME);

        var passwordPropertyEntry = ConfigurationEntry.property(passwordProperty, REDIS_PASSWORD);
        var passwordEnvVariableEntry = ConfigurationEntry.envVariable(passwordEnvVariable, REDIS_PASSWORD);

        var registeredEntries = List.<ConfigurationEntry>of(
                endpointPropertyEntry,
                endpointEnvVariableEntry,
                usernamePropertyEntry,
                usernameEnvVariableEntry,
                passwordPropertyEntry,
                passwordEnvVariableEntry
        );

        assertContainerStarted(container);
        assertContainerExposedPortConfigured(container, REDIS_LISTENING_PORT);
        assertContainerWaitStrategyConfigured(container, Wait.forListeningPort());
        assertContainerLoggingConfigured(container);
        assertContainerCommandExecuted(container, CONFIGURE_REDIS_PASSWORD_COMMAND);
        assertContainerNoEnvVariablesConfigured(container);
        assertName(service, REDIS_SERVICE_NAME);
        assertSingleEndpoint(service, endpoint);
        assertEntriesRegistered(service, registeredEntries);
    }

    @Test
    public void shouldFailStartingServiceWhenPasswordConfigurationFailed() {
        // given
        var usernameProperty = "redis.username";
        var passwordProperty = "redis.password";

        var config = RedisDockerServiceConfig
                .builder()
                .name(REDIS_SERVICE_NAME)
                .dockerImage(REDIS_SERVICE_IMAGE)
                .scheme(REDIS_SCHEME)
                .enablePassword(true)
                .registerEndpointUnderProperties(Set.of())
                .registerEndpointUnderEnvironmentVariables(Set.of())
                .registerUsernameUnderProperties(Set.of(usernameProperty))
                .registerUsernameUnderEnvironmentVariables(Set.of())
                .registerPasswordUnderProperties(Set.of(passwordProperty))
                .registerPasswordUnderEnvironmentVariables(Set.of())
                .build();

        var container = createStubContainer();
        var service = createRedisService(config, container);

        givenContainerAccessible(container);
        givenContainerPasswordSetupFailed(container);

        // when
        // then
        Assertions
                .assertThatThrownBy(() -> service.start(ServiceStartupContext.empty()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Could not configure Redis credentials");
    }

    @Test
    public void shouldShutdownService() {
        // given
        var config = RedisDockerServiceConfig
                .builder()
                .name(REDIS_SERVICE_NAME)
                .dockerImage(REDIS_SERVICE_IMAGE)
                .scheme(REDIS_SCHEME)
                .registerEndpointUnderProperties(Set.of())
                .registerEndpointUnderEnvironmentVariables(Set.of())
                .build();

        var container = createStubContainer();
        var service = createRedisService(config, container);

        // when
        service.shutdown();

        // then
        assertContainerStopped(container);
    }

    private RedisDockerService createRedisService(RedisDockerServiceConfig config,
                                                  GenericContainer<?> container) {
        var configurationRegistry = new MapConfigurationRegistry();
        var endpointRegisterer = new EndpointRegisterer(configurationRegistry);
        var credentialsRegisterer = new RedisCredentialsRegisterer(configurationRegistry);
        var passwordConfigurer = new RedisPasswordConfigurer(container);

        return new RedisDockerService(container, config, configurationRegistry, endpointRegisterer, credentialsRegisterer, passwordConfigurer);
    }

    private GenericContainer<?> createStubContainer() {
        return mock(GenericContainer.class, RETURNS_DEEP_STUBS);
    }

    private void givenContainerAccessible(GenericContainer<?> container) {
        when(container.getHost()).thenReturn(REDIS_HOST);
        when(container.getExposedPorts()).thenReturn(List.of(REDIS_LISTENING_PORT));
        when(container.getMappedPort(REDIS_LISTENING_PORT)).thenReturn(REDIS_MAPPED_PORT);
    }

    private void givenContainerPasswordSetupFailed(GenericContainer<?> container) {
        try {
            var result = mock(Container.ExecResult.class);
            when(result.getExitCode()).thenReturn(CONTAINER_COMMAND_FAILED_CODE);

            when(container.execInContainer(CONFIGURE_REDIS_PASSWORD_COMMAND.split(CONTAINER_COMMAND_SPLITTER))).thenReturn(result);
        } catch (Exception ex) {
            throw new AssertionError("Should not happen", ex);
        }

    }

}
