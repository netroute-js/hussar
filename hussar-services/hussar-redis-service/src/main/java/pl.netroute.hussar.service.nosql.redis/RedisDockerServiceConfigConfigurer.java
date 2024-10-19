package pl.netroute.hussar.service.nosql.redis;

import lombok.Builder;
import lombok.Singular;
import lombok.experimental.SuperBuilder;
import pl.netroute.hussar.core.api.configuration.DefaultConfigurationRegistry;
import pl.netroute.hussar.core.service.BaseDockerServiceConfigurer;
import pl.netroute.hussar.core.service.container.GenericContainerFactory;
import pl.netroute.hussar.core.service.registerer.EndpointRegisterer;
import pl.netroute.hussar.core.service.resolver.DockerImageResolver;
import pl.netroute.hussar.core.service.resolver.ServiceNameResolver;

import java.util.Set;

/**
 * Hussar {@link RedisDockerService} configurer. This is the only way to create {@link RedisDockerService}.
 */
@SuperBuilder(builderMethodName = "newInstance", buildMethodName = "done")
public class RedisDockerServiceConfigConfigurer extends BaseDockerServiceConfigurer<RedisDockerService> {
    private static final String DOCKER_IMAGE = "redis";
    private static final String SERVICE = "redis_service";
    private static final String REDIS_SCHEME = "redis://";

    /**
     * Shall run Redis in password less mode.
     */
    @Builder.Default boolean enablePassword = false;

    /**
     * Set of properties to be used to register Redis username under.
     */
    @Singular
    protected final Set<String> registerUsernameUnderProperties;

    /**
     * Set of environment variables to be used to register Redis username under.
     */
    @Singular
    protected final Set<String> registerUsernameUnderEnvironmentVariables;

    /**
     * Set of properties to be used to register Redis password under.
     */
    @Singular
    protected final Set<String> registerPasswordUnderProperties;

    /**
     * Set of environment variables to be used to register Redis password under.
     */
    @Singular
    protected final Set<String> registerPasswordUnderEnvironmentVariables;

    @Override
    public RedisDockerService configure() {
        var config = createConfig();
        var container = GenericContainerFactory.create(config);
        var configurationRegistry = new DefaultConfigurationRegistry();
        var endpointRegisterer = new EndpointRegisterer(configurationRegistry);
        var credentialsRegisterer = new RedisCredentialsRegisterer(configurationRegistry);
        var passwordConfigurer = new RedisPasswordConfigurer();

        return new RedisDockerService(
                container,
                config,
                configurationRegistry,
                endpointRegisterer,
                credentialsRegisterer,
                passwordConfigurer
        );
    }

    private RedisDockerServiceConfig createConfig() {
        var resolvedName = ServiceNameResolver.resolve(SERVICE, name);
        var resolvedDockerImage = DockerImageResolver.resolve(DOCKER_IMAGE, dockerImageVersion);

        return RedisDockerServiceConfig
                .builder()
                .name(resolvedName)
                .dockerImage(resolvedDockerImage)
                .scheme(REDIS_SCHEME)
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
