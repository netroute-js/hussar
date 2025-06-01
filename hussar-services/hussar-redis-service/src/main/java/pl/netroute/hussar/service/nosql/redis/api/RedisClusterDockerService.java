package pl.netroute.hussar.service.nosql.redis.api;

import lombok.Getter;
import lombok.NonNull;
import org.testcontainers.containers.FixedHostPortGenericContainer;
import org.testcontainers.containers.GenericContainer;
import pl.netroute.hussar.core.configuration.api.ConfigurationRegistry;
import pl.netroute.hussar.core.docker.DockerHostResolver;
import pl.netroute.hussar.core.docker.api.DockerNetwork;
import pl.netroute.hussar.core.network.api.NetworkConfigurer;
import pl.netroute.hussar.core.service.ServiceStartupContext;
import pl.netroute.hussar.core.service.api.BaseDockerService;
import pl.netroute.hussar.core.service.api.Service;
import pl.netroute.hussar.core.service.registerer.EndpointRegisterer;

import java.util.List;
import java.util.stream.IntStream;

import static pl.netroute.hussar.service.nosql.redis.api.RedisClusterSettings.REDIS_CLUSTER_FIRST_PORT;
import static pl.netroute.hussar.service.nosql.redis.api.RedisClusterSettings.REDIS_CLUSTER_IP;
import static pl.netroute.hussar.service.nosql.redis.api.RedisClusterSettings.REDIS_CLUSTER_IP_ENV;
import static pl.netroute.hussar.service.nosql.redis.api.RedisClusterSettings.REDIS_CLUSTER_LOOP_BACK_IP;
import static pl.netroute.hussar.service.nosql.redis.api.RedisClusterSettings.REDIS_CLUSTER_MASTERS;
import static pl.netroute.hussar.service.nosql.redis.api.RedisClusterSettings.REDIS_CLUSTER_MASTERS_ENV;
import static pl.netroute.hussar.service.nosql.redis.api.RedisClusterSettings.REDIS_CLUSTER_NODES;
import static pl.netroute.hussar.service.nosql.redis.api.RedisClusterSettings.REDIS_CLUSTER_PASSWORD;
import static pl.netroute.hussar.service.nosql.redis.api.RedisClusterSettings.REDIS_CLUSTER_SLAVES_PER_MASTER;
import static pl.netroute.hussar.service.nosql.redis.api.RedisClusterSettings.REDIS_CLUSTER_SLAVES_PER_MASTER_ENV;
import static pl.netroute.hussar.service.nosql.redis.api.RedisClusterSettings.REDIS_CLUSTER_USERNAME;

/**
 * Hussar Docker {@link Service} representing RedisCluster.
 */
public class RedisClusterDockerService extends BaseDockerService<RedisClusterDockerServiceConfig> {

    @Getter
    @NonNull
    private final RedisCredentials credentials;

    @NonNull
    private final RedisCredentialsRegisterer credentialsRegisterer;

    @NonNull
    private final RedisPasswordConfigurer passwordConfigurer;

    @NonNull
    private final RedisClusterReplicationPasswordConfigurer clusterReplicationPasswordConfigurer;

    @NonNull
    private final RedisClusterAnnounceIpConfigurer redisClusterAnnounceIpConfigurer;

    @NonNull
    private final RedisClusterNoProtectionConfigurer redisClusterNoProtectionConfigurer;

    @NonNull
    private final RedisClusterWaitStrategy clusterWaitStrategy;

    @NonNull
    private final DockerHostResolver dockerHostResolver;

    @NonNull
    private final List<Integer> clusterNodesPorts;

    RedisClusterDockerService(@NonNull FixedHostPortGenericContainer<?> container,
                              @NonNull DockerNetwork dockerNetwork,
                              @NonNull RedisClusterDockerServiceConfig config,
                              @NonNull ConfigurationRegistry configurationRegistry,
                              @NonNull EndpointRegisterer endpointRegisterer,
                              @NonNull NetworkConfigurer networkConfigurer,
                              @NonNull RedisCredentialsRegisterer credentialsRegisterer,
                              @NonNull RedisPasswordConfigurer passwordConfigurer,
                              @NonNull RedisClusterReplicationPasswordConfigurer clusterReplicationPasswordConfigurer,
                              @NonNull RedisClusterAnnounceIpConfigurer clusterAnnounceIpConfigurer,
                              @NonNull RedisClusterNoProtectionConfigurer redisClusterNoProtectionConfigurer,
                              @NonNull RedisClusterWaitStrategy clusterWaitStrategy,
                              @NonNull DockerHostResolver dockerHostResolver) {
        super(container, dockerNetwork, config, configurationRegistry, endpointRegisterer, networkConfigurer);

        if(isClusterPasswordEnabled()) {
            this.credentials = new RedisCredentials(REDIS_CLUSTER_USERNAME, REDIS_CLUSTER_PASSWORD);
        } else {
            this.credentials = RedisCredentials.passwordLess(REDIS_CLUSTER_USERNAME);
        }

        this.credentialsRegisterer = credentialsRegisterer;
        this.passwordConfigurer = passwordConfigurer;
        this.clusterReplicationPasswordConfigurer = clusterReplicationPasswordConfigurer;
        this.redisClusterAnnounceIpConfigurer = clusterAnnounceIpConfigurer;
        this.redisClusterNoProtectionConfigurer = redisClusterNoProtectionConfigurer;
        this.clusterWaitStrategy = clusterWaitStrategy;
        this.dockerHostResolver = dockerHostResolver;

        this.clusterNodesPorts = IntStream
                .range(0, REDIS_CLUSTER_NODES)
                .map(node -> REDIS_CLUSTER_FIRST_PORT + node)
                .boxed()
                .toList();
    }

    @Override
    protected List<Integer> getInternalPorts() {
        return clusterNodesPorts;
    }

    @Override
    protected void configureContainer(GenericContainer<?> container) {
        super.configureContainer(container);

        var fixedHostPortContainer = (FixedHostPortGenericContainer<?>) container;

        if(!dockerHostResolver.isLocalHost()) {
            configureClusterDockerHost(fixedHostPortContainer);
        }
    }

    @Override
    protected void configureExposedPorts(GenericContainer<?> container) {
        var fixedHostPortContainer = (FixedHostPortGenericContainer<?>) container;

        getInternalPorts().forEach(port -> fixedHostPortContainer.withFixedExposedPort(port, port));
    }

    @Override
    protected void configureEnvVariables(GenericContainer<?> container) {
        super.configureEnvVariables(container);

        var fixedHostPortContainer = (FixedHostPortGenericContainer<?>) container;
        fixedHostPortContainer.withEnv(REDIS_CLUSTER_IP_ENV, REDIS_CLUSTER_IP);
        fixedHostPortContainer.withEnv(REDIS_CLUSTER_MASTERS_ENV, REDIS_CLUSTER_MASTERS + "");
        fixedHostPortContainer.withEnv(REDIS_CLUSTER_SLAVES_PER_MASTER_ENV, REDIS_CLUSTER_SLAVES_PER_MASTER + "");
    }

    @Override
    protected void doAfterServiceStartup(ServiceStartupContext context) {
        super.doAfterServiceStartup(context);

        var fixedHostPortContainer = (FixedHostPortGenericContainer<?>) container;

        if(!dockerHostResolver.isLocalHost()) {
            configureClusterAvailability(fixedHostPortContainer);
        }

        if(isClusterPasswordEnabled()) {
            configureClusterPassword(fixedHostPortContainer, credentials);
        }

        disableProtectionMode(fixedHostPortContainer);

        registerCredentialsUnderProperties();
        registerCredentialsUnderEnvironmentVariables();
    }

    @Override
    protected void configureWaitStrategy(GenericContainer<?> container) {
        container.waitingFor(clusterWaitStrategy);
    }

    private boolean isClusterPasswordEnabled() {
        return config.isEnablePassword();
    }

    private void configureClusterDockerHost(FixedHostPortGenericContainer<?> container) {
        var dockerHost = dockerHostResolver.getHost();

        container.withExtraHost(dockerHost, REDIS_CLUSTER_LOOP_BACK_IP);
    }

    private void configureClusterAvailability(FixedHostPortGenericContainer<?> container) {
        var dockerHost = dockerHostResolver.getHost();

        redisClusterAnnounceIpConfigurer.configure(dockerHost, container);
    }

    private void configureClusterPassword(FixedHostPortGenericContainer<?> container,
                                          RedisCredentials credentials) {
        clusterReplicationPasswordConfigurer.configure(credentials, container);
        passwordConfigurer.configure(credentials, container);
    }

    private void disableProtectionMode(FixedHostPortGenericContainer<?> container) {
        redisClusterNoProtectionConfigurer.configure(container);
    }

    private void registerCredentialsUnderProperties() {
        config.getRegisterUsernameUnderProperties()
                .forEach(usernameProperty -> credentialsRegisterer.registerUsernameUnderProperty(credentials, usernameProperty));

        config.getRegisterPasswordUnderProperties()
                .forEach(passwordProperty -> credentialsRegisterer.registerPasswordUnderProperty(credentials, passwordProperty));
    }

    private void registerCredentialsUnderEnvironmentVariables() {
        config.getRegisterUsernameUnderEnvironmentVariables()
                .forEach(usernameEnvVariable -> credentialsRegisterer.registerUsernameUnderEnvironmentVariable(credentials, usernameEnvVariable));

        config.getRegisterPasswordUnderEnvironmentVariables()
                .forEach(passwordEnvVariable -> credentialsRegisterer.registerPasswordUnderEnvironmentVariable(credentials, passwordEnvVariable));
    }

}
