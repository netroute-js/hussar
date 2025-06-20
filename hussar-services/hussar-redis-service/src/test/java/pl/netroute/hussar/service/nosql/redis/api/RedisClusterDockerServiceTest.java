package pl.netroute.hussar.service.nosql.redis.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.FixedHostPortGenericContainer;
import pl.netroute.hussar.core.api.Endpoint;
import pl.netroute.hussar.core.configuration.api.ConfigurationEntry;
import pl.netroute.hussar.core.configuration.api.DefaultConfigurationRegistry;
import pl.netroute.hussar.core.docker.DockerHostResolver;
import pl.netroute.hussar.core.docker.api.DockerNetwork;
import pl.netroute.hussar.core.network.api.Network;
import pl.netroute.hussar.core.network.api.NetworkConfigurer;
import pl.netroute.hussar.core.service.ServiceStartupContext;
import pl.netroute.hussar.core.service.registerer.EndpointRegisterer;
import pl.netroute.hussar.core.stub.helper.StubHelper;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static pl.netroute.hussar.core.helper.SchemesHelper.EMPTY_SCHEME;
import static pl.netroute.hussar.core.service.assertion.GenericContainerAssertionHelper.assertContainerEnvVariablesConfigured;
import static pl.netroute.hussar.core.service.assertion.GenericContainerAssertionHelper.assertContainerExposedPortConfigured;
import static pl.netroute.hussar.core.service.assertion.GenericContainerAssertionHelper.assertContainerExtraHostConfigured;
import static pl.netroute.hussar.core.service.assertion.GenericContainerAssertionHelper.assertContainerLoggingConfigured;
import static pl.netroute.hussar.core.service.assertion.GenericContainerAssertionHelper.assertContainerNetworkConfigured;
import static pl.netroute.hussar.core.service.assertion.GenericContainerAssertionHelper.assertContainerStarted;
import static pl.netroute.hussar.core.service.assertion.GenericContainerAssertionHelper.assertContainerStartupTimeoutConfigured;
import static pl.netroute.hussar.core.service.assertion.GenericContainerAssertionHelper.assertContainerStopped;
import static pl.netroute.hussar.core.service.assertion.GenericContainerAssertionHelper.assertContainerWaitStrategyConfigured;
import static pl.netroute.hussar.core.service.assertion.GenericContainerAssertionHelper.assertNoContainerExtraHostConfigured;
import static pl.netroute.hussar.core.service.assertion.ServiceAssertionHelper.assertDirectEndpoints;
import static pl.netroute.hussar.core.service.assertion.ServiceAssertionHelper.assertEndpoints;
import static pl.netroute.hussar.core.service.assertion.ServiceAssertionHelper.assertEntriesRegistered;
import static pl.netroute.hussar.core.service.assertion.ServiceAssertionHelper.assertName;
import static pl.netroute.hussar.core.service.assertion.ServiceAssertionHelper.assertNetworkControl;
import static pl.netroute.hussar.core.service.assertion.ServiceAssertionHelper.assertNoEntriesRegistered;
import static pl.netroute.hussar.core.service.registerer.EndpointRegisterer.JOIN_DELIMITER;
import static pl.netroute.hussar.core.stub.helper.DockerHostResolverStubHelper.NON_LOCALHOST;
import static pl.netroute.hussar.core.stub.helper.DockerHostResolverStubHelper.givenDockerLocalhost;
import static pl.netroute.hussar.core.stub.helper.DockerHostResolverStubHelper.givenDockerNonLocalhost;
import static pl.netroute.hussar.core.stub.helper.NetworkConfigurerStubHelper.givenNetworkConfigured;
import static pl.netroute.hussar.service.nosql.redis.api.RedisClusterAnnounceIpConfigurerAssertionHelper.assertClusterAnnounceIpConfigured;
import static pl.netroute.hussar.service.nosql.redis.api.RedisClusterAnnounceIpConfigurerAssertionHelper.assertNoClusterAnnounceIpConfigured;
import static pl.netroute.hussar.service.nosql.redis.api.RedisClusterAnnounceIpConfigurerStubHelper.givenClusterAnnounceIpConfigurationFails;
import static pl.netroute.hussar.service.nosql.redis.api.RedisClusterNoProtectionConfigurerAssertionHelper.assertClusterNoProtectionConfigured;
import static pl.netroute.hussar.service.nosql.redis.api.RedisClusterNoProtectionConfigurerStubHelper.givenNoProtectionConfigurationFails;
import static pl.netroute.hussar.service.nosql.redis.api.RedisClusterReplicationPasswordConfigurerAssertionHelper.assertNoReplicationPasswordConfigured;
import static pl.netroute.hussar.service.nosql.redis.api.RedisClusterReplicationPasswordConfigurerAssertionHelper.assertReplicationPasswordConfigured;
import static pl.netroute.hussar.service.nosql.redis.api.RedisClusterReplicationPasswordConfigurerStubHelper.givenReplicationPasswordConfigurationFails;
import static pl.netroute.hussar.service.nosql.redis.api.RedisClusterSettings.REDIS_CLUSTER_IP;
import static pl.netroute.hussar.service.nosql.redis.api.RedisClusterSettings.REDIS_CLUSTER_IP_ENV;
import static pl.netroute.hussar.service.nosql.redis.api.RedisClusterSettings.REDIS_CLUSTER_LOOP_BACK_IP;
import static pl.netroute.hussar.service.nosql.redis.api.RedisClusterSettings.REDIS_CLUSTER_MASTERS;
import static pl.netroute.hussar.service.nosql.redis.api.RedisClusterSettings.REDIS_CLUSTER_MASTERS_ENV;
import static pl.netroute.hussar.service.nosql.redis.api.RedisClusterSettings.REDIS_CLUSTER_PASSWORD;
import static pl.netroute.hussar.service.nosql.redis.api.RedisClusterSettings.REDIS_CLUSTER_SLAVES_PER_MASTER;
import static pl.netroute.hussar.service.nosql.redis.api.RedisClusterSettings.REDIS_CLUSTER_SLAVES_PER_MASTER_ENV;
import static pl.netroute.hussar.service.nosql.redis.api.RedisClusterSettings.REDIS_CLUSTER_USERNAME;
import static pl.netroute.hussar.service.nosql.redis.api.RedisPasswordConfigurerAssertionHelper.assertNoPasswordConfigured;
import static pl.netroute.hussar.service.nosql.redis.api.RedisPasswordConfigurerAssertionHelper.assertPasswordConfigured;
import static pl.netroute.hussar.service.nosql.redis.api.RedisPasswordConfigurerStubHelper.givenPasswordConfigurationFails;
import static pl.netroute.hussar.service.nosql.redis.api.RedisSettings.REDIS_PASSWORD;

public class RedisClusterDockerServiceTest {
    private static final Integer[] REDIS_CLUSTER_LISTENING_PORTS = new Integer[] {7000, 7001, 7002, 7003, 7004, 7005};

    private static final Duration REDIS_CLUSTER_STARTUP_TIMEOUT = Duration.ofSeconds(90);

    private static final String REDIS_CLUSTER_SERVICE_NAME = "redis-cluster-service";
    private static final String REDIS_CLUSTER_SERVICE_IMAGE = "grokzen/redis-cluster";

    private static final String REDIS_CLUSTER_DIRECT_NETWORK = "direct-" + REDIS_CLUSTER_SERVICE_NAME;

    private DockerNetwork dockerNetwork;
    private NetworkConfigurer networkConfigurer;
    private RedisClusterAnnounceIpConfigurer clusterAnnounceIpConfigurer;
    private RedisClusterReplicationPasswordConfigurer clusterReplicationPasswordConfigurer;
    private RedisClusterNoProtectionConfigurer clusterNoProtectionConfigurer;
    private RedisClusterWaitStrategy clusterWaitStrategy;
    private RedisPasswordConfigurer passwordConfigurer;
    private DockerHostResolver dockerHostResolver;

    @BeforeEach
    public void setup() {
        dockerNetwork = StubHelper.defaultStub(DockerNetwork.class);
        networkConfigurer = StubHelper.defaultStub(NetworkConfigurer.class);
        clusterAnnounceIpConfigurer = StubHelper.defaultStub(RedisClusterAnnounceIpConfigurer.class);
        clusterReplicationPasswordConfigurer = StubHelper.defaultStub(RedisClusterReplicationPasswordConfigurer.class);
        clusterNoProtectionConfigurer = StubHelper.defaultStub(RedisClusterNoProtectionConfigurer.class);
        clusterWaitStrategy = StubHelper.defaultStub(RedisClusterWaitStrategy.class);
        passwordConfigurer = StubHelper.defaultStub(RedisPasswordConfigurer.class);
        dockerHostResolver = StubHelper.defaultStub(DockerHostResolver.class);
    }

    @Test
    public void shouldStartMinimalService() {
        // given
        var config = RedisClusterDockerServiceConfig
                .builder()
                .name(REDIS_CLUSTER_SERVICE_NAME)
                .dockerImage(REDIS_CLUSTER_SERVICE_IMAGE)
                .startupTimeout(REDIS_CLUSTER_STARTUP_TIMEOUT)
                .scheme(EMPTY_SCHEME)
                .registerEndpointUnderProperties(Set.of())
                .registerEndpointUnderEnvironmentVariables(Set.of())
                .registerUsernameUnderProperties(Set.of())
                .registerUsernameUnderEnvironmentVariables(Set.of())
                .registerPasswordUnderProperties(Set.of())
                .registerPasswordUnderEnvironmentVariables(Set.of())
                .build();

        var container = StubHelper.defaultStub(FixedHostPortGenericContainer.class);
        var service = createRedisClusterService(config, container);

        var network = givenNetworkConfigured(networkConfigurer, REDIS_CLUSTER_SERVICE_NAME, EMPTY_SCHEME, REDIS_CLUSTER_LISTENING_PORTS);
        var directNetwork = givenNetworkConfigured(networkConfigurer, REDIS_CLUSTER_DIRECT_NETWORK, EMPTY_SCHEME, REDIS_CLUSTER_LISTENING_PORTS);
        givenDockerLocalhost(dockerHostResolver);

        // when
        service.start(ServiceStartupContext.defaultContext());

        // then
        var envVariables = Map.of(
                REDIS_CLUSTER_IP_ENV, REDIS_CLUSTER_IP,
                REDIS_CLUSTER_MASTERS_ENV, REDIS_CLUSTER_MASTERS + "",
                REDIS_CLUSTER_SLAVES_PER_MASTER_ENV, REDIS_CLUSTER_SLAVES_PER_MASTER + ""
        );

        assertContainerStarted(container);
        assertContainerExposedPortConfigured(container, REDIS_CLUSTER_LISTENING_PORTS);
        assertContainerWaitStrategyConfigured(container, clusterWaitStrategy);
        assertContainerStartupTimeoutConfigured(container, REDIS_CLUSTER_STARTUP_TIMEOUT);
        assertNoContainerExtraHostConfigured(container, NON_LOCALHOST, REDIS_CLUSTER_LOOP_BACK_IP);
        assertContainerNetworkConfigured(container, dockerNetwork);
        assertContainerLoggingConfigured(container);
        assertContainerEnvVariablesConfigured(container, envVariables);
        assertName(service, REDIS_CLUSTER_SERVICE_NAME);
        assertEndpoints(service, network);
        assertDirectEndpoints(service, directNetwork);
        assertNetworkControl(service);
        assertNoEntriesRegistered(service);
        assertNoPasswordConfigured(passwordConfigurer);
        assertNoReplicationPasswordConfigured(clusterReplicationPasswordConfigurer);
        assertNoClusterAnnounceIpConfigured(clusterAnnounceIpConfigurer);
        assertClusterNoProtectionConfigured(clusterNoProtectionConfigurer, container);
    }

    @Test
    public void shouldStartExtendedService() {
        // given
        var endpointsProperty = "endpoints.url";
        var endpointsEnvVariable = "ENDPOINTS_URL";

        var usernameProperty = "redis.cluster.username";
        var usernameEnvVariable = "REDIS_USERNAME";

        var passwordProperty = "redis.cluster.password";
        var passwordEnvVariable = "REDIS_PASSWORD";

        var config = RedisClusterDockerServiceConfig
                .builder()
                .name(REDIS_CLUSTER_SERVICE_NAME)
                .dockerImage(REDIS_CLUSTER_SERVICE_IMAGE)
                .startupTimeout(REDIS_CLUSTER_STARTUP_TIMEOUT)
                .scheme(EMPTY_SCHEME)
                .enablePassword(true)
                .registerEndpointUnderProperties(Set.of(endpointsProperty))
                .registerEndpointUnderEnvironmentVariables(Set.of(endpointsEnvVariable))
                .registerUsernameUnderProperties(Set.of(usernameProperty))
                .registerUsernameUnderEnvironmentVariables(Set.of(usernameEnvVariable))
                .registerPasswordUnderProperties(Set.of(passwordProperty))
                .registerPasswordUnderEnvironmentVariables(Set.of(passwordEnvVariable))
                .build();

        var container = StubHelper.defaultStub(FixedHostPortGenericContainer.class);
        var service = createRedisClusterService(config, container);

        var network = givenNetworkConfigured(networkConfigurer, REDIS_CLUSTER_SERVICE_NAME, EMPTY_SCHEME, REDIS_CLUSTER_LISTENING_PORTS);
        var directNetwork = givenNetworkConfigured(networkConfigurer, REDIS_CLUSTER_DIRECT_NETWORK, EMPTY_SCHEME, REDIS_CLUSTER_LISTENING_PORTS);
        givenDockerLocalhost(dockerHostResolver);

        // when
        service.start(ServiceStartupContext.defaultContext());

        // then
        var endpoints = formatEndpoints(network);
        var endpointsPropertyEntry = ConfigurationEntry.property(endpointsProperty, endpoints);
        var endpointsEnvVariableEntry = ConfigurationEntry.envVariable(endpointsEnvVariable, endpoints);

        var usernamePropertyEntry = ConfigurationEntry.property(usernameProperty, REDIS_CLUSTER_USERNAME);
        var usernameEnvVariableEntry = ConfigurationEntry.envVariable(usernameEnvVariable, REDIS_CLUSTER_USERNAME);

        var passwordPropertyEntry = ConfigurationEntry.property(passwordProperty, REDIS_CLUSTER_PASSWORD);
        var passwordEnvVariableEntry = ConfigurationEntry.envVariable(passwordEnvVariable, REDIS_CLUSTER_PASSWORD);

        var registeredEntries = List.<ConfigurationEntry>of(
                endpointsPropertyEntry,
                endpointsEnvVariableEntry,
                usernamePropertyEntry,
                usernameEnvVariableEntry,
                passwordPropertyEntry,
                passwordEnvVariableEntry
        );

        var envVariables = Map.of(
                REDIS_CLUSTER_IP_ENV, REDIS_CLUSTER_IP,
                REDIS_CLUSTER_MASTERS_ENV, REDIS_CLUSTER_MASTERS + "",
                REDIS_CLUSTER_SLAVES_PER_MASTER_ENV, REDIS_CLUSTER_SLAVES_PER_MASTER + ""
        );

        var credentials = new RedisCredentials(REDIS_CLUSTER_USERNAME, REDIS_PASSWORD);

        assertContainerStarted(container);
        assertContainerExposedPortConfigured(container, REDIS_CLUSTER_LISTENING_PORTS);
        assertContainerWaitStrategyConfigured(container, clusterWaitStrategy);
        assertContainerStartupTimeoutConfigured(container, REDIS_CLUSTER_STARTUP_TIMEOUT);
        assertNoContainerExtraHostConfigured(container, NON_LOCALHOST, REDIS_CLUSTER_LOOP_BACK_IP);
        assertContainerNetworkConfigured(container, dockerNetwork);
        assertContainerLoggingConfigured(container);
        assertContainerEnvVariablesConfigured(container, envVariables);
        assertName(service, REDIS_CLUSTER_SERVICE_NAME);
        assertEndpoints(service, network);
        assertDirectEndpoints(service, directNetwork);
        assertNetworkControl(service);
        assertEntriesRegistered(service, registeredEntries);
        assertPasswordConfigured(passwordConfigurer, credentials, container);
        assertReplicationPasswordConfigured(clusterReplicationPasswordConfigurer, credentials, container);
        assertNoClusterAnnounceIpConfigured(clusterAnnounceIpConfigurer);
        assertClusterNoProtectionConfigured(clusterNoProtectionConfigurer, container);
    }

    @Test
    public void shouldStartServiceInNonLocalhostDockerEnvironment() {
        // given
        var config = RedisClusterDockerServiceConfig
                .builder()
                .name(REDIS_CLUSTER_SERVICE_NAME)
                .dockerImage(REDIS_CLUSTER_SERVICE_IMAGE)
                .startupTimeout(REDIS_CLUSTER_STARTUP_TIMEOUT)
                .scheme(EMPTY_SCHEME)
                .registerEndpointUnderProperties(Set.of())
                .registerEndpointUnderEnvironmentVariables(Set.of())
                .registerUsernameUnderProperties(Set.of())
                .registerUsernameUnderEnvironmentVariables(Set.of())
                .registerPasswordUnderProperties(Set.of())
                .registerPasswordUnderEnvironmentVariables(Set.of())
                .build();

        var container = StubHelper.defaultStub(FixedHostPortGenericContainer.class);
        var service = createRedisClusterService(config, container);

        var network = givenNetworkConfigured(networkConfigurer, REDIS_CLUSTER_SERVICE_NAME, EMPTY_SCHEME, REDIS_CLUSTER_LISTENING_PORTS);
        var directNetwork = givenNetworkConfigured(networkConfigurer, REDIS_CLUSTER_DIRECT_NETWORK, EMPTY_SCHEME, REDIS_CLUSTER_LISTENING_PORTS);
        givenDockerNonLocalhost(dockerHostResolver);

        // when
        service.start(ServiceStartupContext.defaultContext());

        // then
        var envVariables = Map.of(
                REDIS_CLUSTER_IP_ENV, REDIS_CLUSTER_IP,
                REDIS_CLUSTER_MASTERS_ENV, REDIS_CLUSTER_MASTERS + "",
                REDIS_CLUSTER_SLAVES_PER_MASTER_ENV, REDIS_CLUSTER_SLAVES_PER_MASTER + ""
        );

        assertContainerStarted(container);
        assertContainerExposedPortConfigured(container, REDIS_CLUSTER_LISTENING_PORTS);
        assertContainerExtraHostConfigured(container, NON_LOCALHOST, REDIS_CLUSTER_LOOP_BACK_IP);
        assertContainerWaitStrategyConfigured(container, clusterWaitStrategy);
        assertContainerStartupTimeoutConfigured(container, REDIS_CLUSTER_STARTUP_TIMEOUT);
        assertContainerNetworkConfigured(container, dockerNetwork);
        assertContainerLoggingConfigured(container);
        assertContainerEnvVariablesConfigured(container, envVariables);
        assertName(service, REDIS_CLUSTER_SERVICE_NAME);
        assertEndpoints(service, network);
        assertDirectEndpoints(service, directNetwork);
        assertNetworkControl(service);
        assertNoEntriesRegistered(service);
        assertNoPasswordConfigured(passwordConfigurer);
        assertNoReplicationPasswordConfigured(clusterReplicationPasswordConfigurer);
        assertClusterAnnounceIpConfigured(clusterAnnounceIpConfigurer, NON_LOCALHOST, container);
        assertClusterNoProtectionConfigured(clusterNoProtectionConfigurer, container);
    }

    @Test
    public void shouldFailStartingServiceWhenPasswordConfigurationFailed() {
        // given
        var config = RedisClusterDockerServiceConfig
                .builder()
                .name(REDIS_CLUSTER_SERVICE_NAME)
                .dockerImage(REDIS_CLUSTER_SERVICE_IMAGE)
                .startupTimeout(REDIS_CLUSTER_STARTUP_TIMEOUT)
                .scheme(EMPTY_SCHEME)
                .enablePassword(true)
                .registerEndpointUnderProperties(Set.of())
                .registerEndpointUnderEnvironmentVariables(Set.of())
                .registerUsernameUnderProperties(Set.of())
                .registerUsernameUnderEnvironmentVariables(Set.of())
                .registerPasswordUnderProperties(Set.of())
                .registerPasswordUnderEnvironmentVariables(Set.of())
                .build();

        var container = StubHelper.defaultStub(FixedHostPortGenericContainer.class);
        var service = createRedisClusterService(config, container);

        givenDockerLocalhost(dockerHostResolver);
        givenPasswordConfigurationFails(passwordConfigurer, container);

        // when
        // then
        assertThatThrownBy(() -> service.start(ServiceStartupContext.defaultContext()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Docker command has failed with [-1] code");
    }

    @Test
    public void shouldFailStartingServiceWhenReplicationPasswordConfigurationFailed() {
        // given
        var config = RedisClusterDockerServiceConfig
                .builder()
                .name(REDIS_CLUSTER_SERVICE_NAME)
                .dockerImage(REDIS_CLUSTER_SERVICE_IMAGE)
                .startupTimeout(REDIS_CLUSTER_STARTUP_TIMEOUT)
                .scheme(EMPTY_SCHEME)
                .enablePassword(true)
                .registerEndpointUnderProperties(Set.of())
                .registerEndpointUnderEnvironmentVariables(Set.of())
                .registerUsernameUnderProperties(Set.of())
                .registerUsernameUnderEnvironmentVariables(Set.of())
                .registerPasswordUnderProperties(Set.of())
                .registerPasswordUnderEnvironmentVariables(Set.of())
                .build();

        var container = StubHelper.defaultStub(FixedHostPortGenericContainer.class);
        var service = createRedisClusterService(config, container);

        givenDockerLocalhost(dockerHostResolver);
        givenReplicationPasswordConfigurationFails(clusterReplicationPasswordConfigurer, container);

        // when
        // then
        assertThatThrownBy(() -> service.start(ServiceStartupContext.defaultContext()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Docker command has failed with [-1] code");
    }

    @Test
    public void shouldFailStartingServiceWhenClusterAnnounceIpConfigurationFailed() {
        // given
        var config = RedisClusterDockerServiceConfig
                .builder()
                .name(REDIS_CLUSTER_SERVICE_NAME)
                .dockerImage(REDIS_CLUSTER_SERVICE_IMAGE)
                .startupTimeout(REDIS_CLUSTER_STARTUP_TIMEOUT)
                .scheme(EMPTY_SCHEME)
                .registerEndpointUnderProperties(Set.of())
                .registerEndpointUnderEnvironmentVariables(Set.of())
                .registerUsernameUnderProperties(Set.of())
                .registerUsernameUnderEnvironmentVariables(Set.of())
                .registerPasswordUnderProperties(Set.of())
                .registerPasswordUnderEnvironmentVariables(Set.of())
                .build();

        var container = StubHelper.defaultStub(FixedHostPortGenericContainer.class);
        var service = createRedisClusterService(config, container);

        givenDockerNonLocalhost(dockerHostResolver);
        givenClusterAnnounceIpConfigurationFails(clusterAnnounceIpConfigurer, container);

        // when
        // then
        assertThatThrownBy(() -> service.start(ServiceStartupContext.defaultContext()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Docker command has failed with [-1] code");
    }

    @Test
    public void shouldFailStartingServiceWhenClusterNoProtectionConfigurationFailed() {
        // given
        var config = RedisClusterDockerServiceConfig
                .builder()
                .name(REDIS_CLUSTER_SERVICE_NAME)
                .dockerImage(REDIS_CLUSTER_SERVICE_IMAGE)
                .startupTimeout(REDIS_CLUSTER_STARTUP_TIMEOUT)
                .scheme(EMPTY_SCHEME)
                .registerEndpointUnderProperties(Set.of())
                .registerEndpointUnderEnvironmentVariables(Set.of())
                .registerUsernameUnderProperties(Set.of())
                .registerUsernameUnderEnvironmentVariables(Set.of())
                .registerPasswordUnderProperties(Set.of())
                .registerPasswordUnderEnvironmentVariables(Set.of())
                .build();

        var container = StubHelper.defaultStub(FixedHostPortGenericContainer.class);
        var service = createRedisClusterService(config, container);

        givenDockerNonLocalhost(dockerHostResolver);
        givenNoProtectionConfigurationFails(clusterNoProtectionConfigurer, container);

        // when
        // then
        assertThatThrownBy(() -> service.start(ServiceStartupContext.defaultContext()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Docker command has failed with [-1] code");
    }

    @Test
    public void shouldShutdownService() {
        // given
        var config = RedisClusterDockerServiceConfig
                .builder()
                .name(REDIS_CLUSTER_SERVICE_NAME)
                .dockerImage(REDIS_CLUSTER_SERVICE_IMAGE)
                .startupTimeout(REDIS_CLUSTER_STARTUP_TIMEOUT)
                .scheme(EMPTY_SCHEME)
                .registerEndpointUnderProperties(Set.of())
                .registerEndpointUnderEnvironmentVariables(Set.of())
                .registerUsernameUnderProperties(Set.of())
                .registerUsernameUnderEnvironmentVariables(Set.of())
                .registerPasswordUnderProperties(Set.of())
                .registerPasswordUnderEnvironmentVariables(Set.of())
                .build();

        var container = StubHelper.defaultStub(FixedHostPortGenericContainer.class);
        var service = createRedisClusterService(config, container);

        // when
        service.shutdown();

        // then
        assertContainerStopped(container);
    }

    @Test
    public void shouldGetPasswordLessCredentials() {
        // given
        var config = RedisClusterDockerServiceConfig
                .builder()
                .name(REDIS_CLUSTER_SERVICE_NAME)
                .dockerImage(REDIS_CLUSTER_SERVICE_IMAGE)
                .startupTimeout(REDIS_CLUSTER_STARTUP_TIMEOUT)
                .scheme(EMPTY_SCHEME)
                .registerEndpointUnderProperties(Set.of())
                .registerEndpointUnderEnvironmentVariables(Set.of())
                .registerUsernameUnderProperties(Set.of())
                .registerUsernameUnderEnvironmentVariables(Set.of())
                .registerPasswordUnderProperties(Set.of())
                .registerPasswordUnderEnvironmentVariables(Set.of())
                .build();

        var container = StubHelper.defaultStub(FixedHostPortGenericContainer.class);
        var service = createRedisClusterService(config, container);

        // when
        var credentials = service.getCredentials();

        // then
        assertPasswordLessCredentials(credentials);
    }

    @Test
    public void shouldGetPasswordAwareCredentials() {
        // given
        var config = RedisClusterDockerServiceConfig
                .builder()
                .name(REDIS_CLUSTER_SERVICE_NAME)
                .dockerImage(REDIS_CLUSTER_SERVICE_IMAGE)
                .startupTimeout(REDIS_CLUSTER_STARTUP_TIMEOUT)
                .scheme(EMPTY_SCHEME)
                .enablePassword(true)
                .registerEndpointUnderProperties(Set.of())
                .registerEndpointUnderEnvironmentVariables(Set.of())
                .registerUsernameUnderProperties(Set.of())
                .registerUsernameUnderEnvironmentVariables(Set.of())
                .registerPasswordUnderProperties(Set.of())
                .registerPasswordUnderEnvironmentVariables(Set.of())
                .build();

        var container = StubHelper.defaultStub(FixedHostPortGenericContainer.class);
        var service = createRedisClusterService(config, container);

        // when
        var credentials = service.getCredentials();

        // then
        assertPasswordAwareCredentials(credentials);
    }

    private RedisClusterDockerService createRedisClusterService(RedisClusterDockerServiceConfig config,
                                                                FixedHostPortGenericContainer<?> container) {
        var configurationRegistry = new DefaultConfigurationRegistry();
        var endpointRegisterer = new EndpointRegisterer(configurationRegistry);
        var credentialsRegisterer = new RedisCredentialsRegisterer(configurationRegistry);

        return new RedisClusterDockerService(
                container,
                dockerNetwork,
                config,
                configurationRegistry,
                endpointRegisterer,
                networkConfigurer,
                credentialsRegisterer,
                passwordConfigurer,
                clusterReplicationPasswordConfigurer,
                clusterAnnounceIpConfigurer,
                clusterNoProtectionConfigurer,
                clusterWaitStrategy,
                dockerHostResolver
        );
    }

    private String formatEndpoints(Network network) {
        return network
                .getEndpoints()
                .stream()
                .map(Endpoint::address)
                .collect(Collectors.joining(JOIN_DELIMITER));
    }

    private void assertPasswordLessCredentials(RedisCredentials credentials) {
        assertThat(credentials.username()).isEqualTo(REDIS_CLUSTER_USERNAME);
        assertThat(credentials.password()).isNull();
    }

    private void assertPasswordAwareCredentials(RedisCredentials credentials) {
        assertThat(credentials.username()).isEqualTo(REDIS_CLUSTER_USERNAME);
        assertThat(credentials.password()).isEqualTo(REDIS_CLUSTER_PASSWORD);
    }

}
