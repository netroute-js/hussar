package pl.netroute.hussar.service.nosql.redis.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import pl.netroute.hussar.core.configuration.api.ConfigurationEntry;
import pl.netroute.hussar.core.configuration.api.DefaultConfigurationRegistry;
import pl.netroute.hussar.core.docker.api.DockerNetwork;
import pl.netroute.hussar.core.network.api.NetworkConfigurer;
import pl.netroute.hussar.core.service.ServiceStartupContext;
import pl.netroute.hussar.core.service.registerer.EndpointRegisterer;
import pl.netroute.hussar.core.stub.helper.StubHelper;

import java.time.Duration;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static pl.netroute.hussar.core.service.assertion.GenericContainerAssertionHelper.assertContainerExposedPortConfigured;
import static pl.netroute.hussar.core.service.assertion.GenericContainerAssertionHelper.assertContainerLoggingConfigured;
import static pl.netroute.hussar.core.service.assertion.GenericContainerAssertionHelper.assertContainerNetworkConfigured;
import static pl.netroute.hussar.core.service.assertion.GenericContainerAssertionHelper.assertContainerNoEnvVariablesConfigured;
import static pl.netroute.hussar.core.service.assertion.GenericContainerAssertionHelper.assertContainerStarted;
import static pl.netroute.hussar.core.service.assertion.GenericContainerAssertionHelper.assertContainerStartupTimeoutConfigured;
import static pl.netroute.hussar.core.service.assertion.GenericContainerAssertionHelper.assertContainerStopped;
import static pl.netroute.hussar.core.service.assertion.GenericContainerAssertionHelper.assertContainerWaitStrategyConfigured;
import static pl.netroute.hussar.core.service.assertion.ServiceAssertionHelper.assertEndpoints;
import static pl.netroute.hussar.core.service.assertion.ServiceAssertionHelper.assertEntriesRegistered;
import static pl.netroute.hussar.core.service.assertion.ServiceAssertionHelper.assertName;
import static pl.netroute.hussar.core.service.assertion.ServiceAssertionHelper.assertNetworkControl;
import static pl.netroute.hussar.core.service.assertion.ServiceAssertionHelper.assertNoEntriesRegistered;
import static pl.netroute.hussar.core.stub.helper.NetworkConfigurerStubHelper.givenNetworkConfigured;
import static pl.netroute.hussar.service.nosql.redis.api.RedisPasswordConfigurerAssertionHelper.assertNoPasswordConfigured;
import static pl.netroute.hussar.service.nosql.redis.api.RedisPasswordConfigurerAssertionHelper.assertPasswordConfigured;
import static pl.netroute.hussar.service.nosql.redis.api.RedisPasswordConfigurerStubHelper.givenPasswordConfigurationFails;
import static pl.netroute.hussar.service.nosql.redis.api.RedisSettings.REDIS_LISTENING_PORT;
import static pl.netroute.hussar.service.nosql.redis.api.RedisSettings.REDIS_PASSWORD;
import static pl.netroute.hussar.service.nosql.redis.api.RedisSettings.REDIS_USERNAME;

public class RedisDockerServiceTest {
    private static final Duration REDIS_STARTUP_TIMEOUT = Duration.ofSeconds(90L);

    private static final String REDIS_SERVICE_NAME = "redis-service";
    private static final String REDIS_SERVICE_IMAGE = "redis";

    private static final String REDIS_SCHEME = "redis://";

    private DockerNetwork dockerNetwork;
    private NetworkConfigurer networkConfigurer;
    private RedisPasswordConfigurer passwordConfigurer;

    @BeforeEach
    public void setup() {
        dockerNetwork = StubHelper.defaultStub(DockerNetwork.class);
        networkConfigurer = StubHelper.defaultStub(NetworkConfigurer.class);
        passwordConfigurer = StubHelper.defaultStub(RedisPasswordConfigurer.class);
    }

    @Test
    public void shouldStartMinimalService() {
        // given
        var config = RedisDockerServiceConfig
                .builder()
                .name(REDIS_SERVICE_NAME)
                .dockerImage(REDIS_SERVICE_IMAGE)
                .startupTimeout(REDIS_STARTUP_TIMEOUT)
                .scheme(REDIS_SCHEME)
                .registerEndpointUnderProperties(Set.of())
                .registerEndpointUnderEnvironmentVariables(Set.of())
                .registerUsernameUnderProperties(Set.of())
                .registerUsernameUnderEnvironmentVariables(Set.of())
                .registerPasswordUnderProperties(Set.of())
                .registerPasswordUnderEnvironmentVariables(Set.of())
                .build();

        var container = StubHelper.defaultStub(GenericContainer.class);
        var service = createRedisService(config, container);

        var network = givenNetworkConfigured(networkConfigurer, REDIS_SERVICE_NAME, REDIS_SCHEME, REDIS_LISTENING_PORT);

        // when
        service.start(ServiceStartupContext.defaultContext());

        // then
        assertContainerStarted(container);
        assertContainerExposedPortConfigured(container, REDIS_LISTENING_PORT);
        assertContainerWaitStrategyConfigured(container, Wait.forListeningPort());
        assertContainerStartupTimeoutConfigured(container, REDIS_STARTUP_TIMEOUT);
        assertContainerNetworkConfigured(container, dockerNetwork);
        assertContainerLoggingConfigured(container);
        assertContainerNoEnvVariablesConfigured(container);
        assertName(service, REDIS_SERVICE_NAME);
        assertEndpoints(service, network);
        assertNetworkControl(service);
        assertNoEntriesRegistered(service);
        assertNoPasswordConfigured(passwordConfigurer);
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
                .startupTimeout(REDIS_STARTUP_TIMEOUT)
                .scheme(REDIS_SCHEME)
                .enablePassword(true)
                .registerEndpointUnderProperties(Set.of(endpointProperty))
                .registerEndpointUnderEnvironmentVariables(Set.of(endpointEnvVariable))
                .registerUsernameUnderProperties(Set.of(usernameProperty))
                .registerUsernameUnderEnvironmentVariables(Set.of(usernameEnvVariable))
                .registerPasswordUnderProperties(Set.of(passwordProperty))
                .registerPasswordUnderEnvironmentVariables(Set.of(passwordEnvVariable))
                .build();

        var container = StubHelper.defaultStub(GenericContainer.class);
        var service = createRedisService(config, container);

        var network = givenNetworkConfigured(networkConfigurer, REDIS_SERVICE_NAME, REDIS_SCHEME, REDIS_LISTENING_PORT);

        // when
        service.start(ServiceStartupContext.defaultContext());

        // then
        var endpoint = network.getEndpoints().getFirst();
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

        var credentials = new RedisCredentials(REDIS_USERNAME, REDIS_PASSWORD);

        assertContainerStarted(container);
        assertContainerExposedPortConfigured(container, REDIS_LISTENING_PORT);
        assertContainerNetworkConfigured(container, dockerNetwork);
        assertContainerWaitStrategyConfigured(container, Wait.forListeningPort());
        assertContainerStartupTimeoutConfigured(container, REDIS_STARTUP_TIMEOUT);
        assertContainerLoggingConfigured(container);
        assertContainerNoEnvVariablesConfigured(container);
        assertName(service, REDIS_SERVICE_NAME);
        assertEndpoints(service, network);
        assertNetworkControl(service);
        assertEntriesRegistered(service, registeredEntries);
        assertPasswordConfigured(passwordConfigurer, credentials, container);
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
                .startupTimeout(REDIS_STARTUP_TIMEOUT)
                .scheme(REDIS_SCHEME)
                .enablePassword(true)
                .registerEndpointUnderProperties(Set.of())
                .registerEndpointUnderEnvironmentVariables(Set.of())
                .registerUsernameUnderProperties(Set.of(usernameProperty))
                .registerUsernameUnderEnvironmentVariables(Set.of())
                .registerPasswordUnderProperties(Set.of(passwordProperty))
                .registerPasswordUnderEnvironmentVariables(Set.of())
                .build();

        var container = StubHelper.defaultStub(GenericContainer.class);
        var service = createRedisService(config, container);

        givenPasswordConfigurationFails(passwordConfigurer, container);

        // when
        // then
        assertThatThrownBy(() -> service.start(ServiceStartupContext.defaultContext()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Docker command has failed with [-1] code");
    }

    @Test
    public void shouldShutdownService() {
        // given
        var config = RedisDockerServiceConfig
                .builder()
                .name(REDIS_SERVICE_NAME)
                .dockerImage(REDIS_SERVICE_IMAGE)
                .startupTimeout(REDIS_STARTUP_TIMEOUT)
                .scheme(REDIS_SCHEME)
                .registerEndpointUnderProperties(Set.of())
                .registerEndpointUnderEnvironmentVariables(Set.of())
                .registerUsernameUnderProperties(Set.of())
                .registerUsernameUnderEnvironmentVariables(Set.of())
                .registerPasswordUnderProperties(Set.of())
                .registerPasswordUnderEnvironmentVariables(Set.of())
                .build();

        var container = StubHelper.defaultStub(GenericContainer.class);
        var service = createRedisService(config, container);

        // when
        service.shutdown();

        // then
        assertContainerStopped(container);
    }

    @Test
    public void shouldGetPasswordLessCredentials() {
        // given
        var config = RedisDockerServiceConfig
                .builder()
                .name(REDIS_SERVICE_NAME)
                .dockerImage(REDIS_SERVICE_IMAGE)
                .startupTimeout(REDIS_STARTUP_TIMEOUT)
                .scheme(REDIS_SCHEME)
                .registerEndpointUnderProperties(Set.of())
                .registerEndpointUnderEnvironmentVariables(Set.of())
                .registerUsernameUnderProperties(Set.of())
                .registerUsernameUnderEnvironmentVariables(Set.of())
                .registerPasswordUnderProperties(Set.of())
                .registerPasswordUnderEnvironmentVariables(Set.of())
                .build();

        var container = StubHelper.defaultStub(GenericContainer.class);
        var service = createRedisService(config, container);

        // when
        var credentials = service.getCredentials();

        // then
        assertPasswordLessCredentials(credentials);
    }

    @Test
    public void shouldGetPasswordAwareCredentials() {
        // given
        var config = RedisDockerServiceConfig
                .builder()
                .name(REDIS_SERVICE_NAME)
                .dockerImage(REDIS_SERVICE_IMAGE)
                .startupTimeout(REDIS_STARTUP_TIMEOUT)
                .scheme(REDIS_SCHEME)
                .enablePassword(true)
                .registerEndpointUnderProperties(Set.of())
                .registerEndpointUnderEnvironmentVariables(Set.of())
                .registerUsernameUnderProperties(Set.of())
                .registerUsernameUnderEnvironmentVariables(Set.of())
                .registerPasswordUnderProperties(Set.of())
                .registerPasswordUnderEnvironmentVariables(Set.of())
                .build();

        var container = StubHelper.defaultStub(GenericContainer.class);
        var service = createRedisService(config, container);

        // when
        var credentials = service.getCredentials();

        // then
        assertPasswordAwareCredentials(credentials);
    }

    private RedisDockerService createRedisService(RedisDockerServiceConfig config,
                                                  GenericContainer<?> container) {
        var configurationRegistry = new DefaultConfigurationRegistry();
        var endpointRegisterer = new EndpointRegisterer(configurationRegistry);
        var credentialsRegisterer = new RedisCredentialsRegisterer(configurationRegistry);

        return new RedisDockerService(
                container,
                dockerNetwork,
                config,
                configurationRegistry,
                endpointRegisterer,
                networkConfigurer,
                credentialsRegisterer,
                passwordConfigurer
        );
    }

    private void assertPasswordLessCredentials(RedisCredentials credentials) {
        assertThat(credentials.username()).isEqualTo(REDIS_USERNAME);
        assertThat(credentials.password()).isNull();
    }

    private void assertPasswordAwareCredentials(RedisCredentials credentials) {
        assertThat(credentials.username()).isEqualTo(REDIS_USERNAME);
        assertThat(credentials.password()).isEqualTo(REDIS_PASSWORD);
    }

}
