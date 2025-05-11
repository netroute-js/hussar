package pl.netroute.hussar.service.nosql.redis.api;

import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;
import lombok.experimental.SuperBuilder;
import org.testcontainers.containers.FixedHostPortGenericContainer;
import org.testcontainers.utility.DockerImageName;
import pl.netroute.hussar.core.configuration.api.DefaultConfigurationRegistry;
import pl.netroute.hussar.core.docker.DockerCommandLineRunner;
import pl.netroute.hussar.core.docker.DockerHostResolver;
import pl.netroute.hussar.core.docker.DockerImageResolver;
import pl.netroute.hussar.core.helper.SchemesHelper;
import pl.netroute.hussar.core.service.ServiceConfigureContext;
import pl.netroute.hussar.core.service.api.BaseDockerServiceConfigurer;
import pl.netroute.hussar.core.service.registerer.EndpointRegisterer;
import pl.netroute.hussar.core.service.resolver.ServiceNameResolver;

import java.util.Set;

/**
 * Hussar {@link RedisClusterDockerService} configurer. This is the only way to create {@link RedisClusterDockerService}.
 */
@SuperBuilder(builderMethodName = "newInstance", buildMethodName = "done")
public class RedisClusterDockerServiceConfigurer extends BaseDockerServiceConfigurer<RedisClusterDockerService> {
    private static final String DOCKER_IMAGE = "grokzen/redis-cluster";
    private static final String SERVICE = "redis_cluster_service";

    /**
     * Shall run RedisCluster in password less mode.
     */
    @Builder.Default
    protected boolean enablePassword = false;

    /**
     * Set of properties to be used to register RedisCluster username under.
     */
    @Singular
    protected final Set<String> registerUsernameUnderProperties;

    /**
     * Set of environment variables to be used to register RedisCluster username under.
     */
    @Singular
    protected final Set<String> registerUsernameUnderEnvironmentVariables;

    /**
     * Set of properties to be used to register RedisCluster password under.
     */
    @Singular
    protected final Set<String> registerPasswordUnderProperties;

    /**
     * Set of environment variables to be used to register RedisCluster password under.
     */
    @Singular
    protected final Set<String> registerPasswordUnderEnvironmentVariables;

    @Override
    public RedisClusterDockerService configure(@NonNull ServiceConfigureContext context) {
        var dockerRegistry = context.dockerRegistry();
        var dockerImage = DockerImageResolver.resolve(dockerRegistry, DOCKER_IMAGE, dockerImageVersion);
        var dockerCommandLineRunner = new DockerCommandLineRunner();
        var config = createConfig(dockerImage);
        var container = new FixedHostPortGenericContainer<>(dockerImage.asCanonicalNameString());
        var configurationRegistry = new DefaultConfigurationRegistry();
        var endpointRegisterer = new EndpointRegisterer(configurationRegistry);
        var credentialsRegisterer = new RedisCredentialsRegisterer(configurationRegistry);
        var passwordConfigurer = new RedisPasswordConfigurer(dockerCommandLineRunner);
        var clusterReplicationPasswordConfigurer = new RedisClusterReplicationPasswordConfigurer(dockerCommandLineRunner);
        var clusterAnnounceIpConfigurer = new RedisClusterAnnounceIpConfigurer(dockerCommandLineRunner);
        var clusterNoProtectionConfigurer = new RedisClusterNoProtectionConfigurer(dockerCommandLineRunner);
        var clusterWaitStrategy = new RedisClusterWaitStrategy(dockerCommandLineRunner);
        var dockerHostResolver = new DockerHostResolver();

        return new RedisClusterDockerService(
                container,
                config,
                configurationRegistry,
                endpointRegisterer,
                credentialsRegisterer,
                passwordConfigurer,
                clusterReplicationPasswordConfigurer,
                clusterAnnounceIpConfigurer,
                clusterNoProtectionConfigurer,
                clusterWaitStrategy,
                dockerHostResolver
        );
    }

    private RedisClusterDockerServiceConfig createConfig(DockerImageName dockerImage) {
        var resolvedName = ServiceNameResolver.resolve(SERVICE, name);

        return RedisClusterDockerServiceConfig
                .builder()
                .name(resolvedName)
                .dockerImage(dockerImage.asCanonicalNameString())
                .scheme(SchemesHelper.EMPTY_SCHEME)
                .enablePassword(enablePassword)
                .registerUsernameUnderProperties(registerUsernameUnderProperties)
                .registerUsernameUnderEnvironmentVariables(registerUsernameUnderEnvironmentVariables)
                .registerPasswordUnderProperties(registerPasswordUnderProperties)
                .registerPasswordUnderEnvironmentVariables(registerPasswordUnderEnvironmentVariables)
                .registerEndpointUnderProperties(registerEndpointUnderProperties)
                .registerEndpointUnderEnvironmentVariables(registerEndpointUnderEnvironmentVariables)
                .build();
    }

}
