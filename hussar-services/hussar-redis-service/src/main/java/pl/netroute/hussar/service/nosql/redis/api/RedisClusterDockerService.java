package pl.netroute.hussar.service.nosql.redis.api;

import lombok.Getter;
import lombok.NonNull;
import org.testcontainers.containers.FixedHostPortGenericContainer;
import org.testcontainers.containers.GenericContainer;
import pl.netroute.hussar.core.api.Endpoint;
import pl.netroute.hussar.core.configuration.api.ConfigurationRegistry;
import pl.netroute.hussar.core.docker.DockerHostResolver;
import pl.netroute.hussar.core.helper.SchemesHelper;
import pl.netroute.hussar.core.service.ServiceStartupContext;
import pl.netroute.hussar.core.service.api.BaseDockerService;
import pl.netroute.hussar.core.service.api.Service;
import pl.netroute.hussar.core.service.registerer.EndpointRegisterer;

import java.util.List;
import java.util.Optional;
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

    RedisClusterDockerService(@NonNull FixedHostPortGenericContainer<?> container,
                              @NonNull RedisClusterDockerServiceConfig config,
                              @NonNull ConfigurationRegistry configurationRegistry,
                              @NonNull EndpointRegisterer endpointRegisterer,
                              @NonNull RedisCredentialsRegisterer credentialsRegisterer,
                              @NonNull RedisPasswordConfigurer passwordConfigurer,
                              @NonNull RedisClusterReplicationPasswordConfigurer clusterReplicationPasswordConfigurer,
                              @NonNull RedisClusterAnnounceIpConfigurer clusterAnnounceIpConfigurer,
                              @NonNull RedisClusterNoProtectionConfigurer redisClusterNoProtectionConfigurer,
                              @NonNull RedisClusterWaitStrategy clusterWaitStrategy,
                              @NonNull DockerHostResolver dockerHostResolver) {
        super(container, config, configurationRegistry, endpointRegisterer);

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
    }

    @Override
    public List<Endpoint> getEndpoints() {
        var host = container.getHost();

        var scheme = Optional
                .ofNullable(config.getScheme())
                .orElse(SchemesHelper.EMPTY_SCHEME);

        return container
                .getBoundPortNumbers()
                .stream()
                .map(mappedPort -> Endpoint.of(scheme, host, mappedPort))
                .toList();
    }

    @Override
    protected void configureContainer(GenericContainer<?> container) {
        super.configureContainer(container);

        var fixedHostPortContainer = (FixedHostPortGenericContainer<?>) container;

        fixedHostPortContainer.withEnv(REDIS_CLUSTER_IP_ENV, REDIS_CLUSTER_IP);
        fixedHostPortContainer.withEnv(REDIS_CLUSTER_MASTERS_ENV, REDIS_CLUSTER_MASTERS + "");
        fixedHostPortContainer.withEnv(REDIS_CLUSTER_SLAVES_PER_MASTER_ENV, REDIS_CLUSTER_SLAVES_PER_MASTER + "");

        if(!dockerHostResolver.isLocalHost()) {
            configureClusterDockerHost(fixedHostPortContainer);
        }

        configureClusterNodesPorts(fixedHostPortContainer);
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
        passwordConfigurer.configure(credentials, container);
        clusterReplicationPasswordConfigurer.configure(credentials, container);
    }

    private void disableProtectionMode(FixedHostPortGenericContainer<?> container) {
        redisClusterNoProtectionConfigurer.configure(container);
    }

    private void configureClusterNodesPorts(FixedHostPortGenericContainer<?> container) {
        getClusterNodesPorts().forEach(port -> container.withFixedExposedPort(port, port));
    }

    private List<Integer> getClusterNodesPorts() {
        return IntStream
                .range(0, REDIS_CLUSTER_NODES)
                .map(node -> REDIS_CLUSTER_FIRST_PORT + node)
                .boxed()
                .toList();
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
