package pl.netroute.hussar.service.nosql.redis.api;

import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;
import lombok.experimental.SuperBuilder;
import org.testcontainers.utility.DockerImageName;
import pl.netroute.hussar.core.configuration.api.DefaultConfigurationRegistry;
import pl.netroute.hussar.core.docker.DockerCommandLineRunner;
import pl.netroute.hussar.core.service.api.BaseDockerServiceConfigurer;
import pl.netroute.hussar.core.docker.GenericContainerFactory;
import pl.netroute.hussar.core.service.ServiceConfigureContext;
import pl.netroute.hussar.core.service.registerer.EndpointRegisterer;
import pl.netroute.hussar.core.docker.DockerImageResolver;
import pl.netroute.hussar.core.service.resolver.ServiceNameResolver;

import java.util.Set;

/**
 * Hussar {@link RedisDockerService} configurer. This is the only way to create {@link RedisDockerService}.
 */
@SuperBuilder(builderMethodName = "newInstance", buildMethodName = "done")
public class RedisDockerServiceConfigurer extends BaseDockerServiceConfigurer<RedisDockerService> {
    private static final String DOCKER_IMAGE = "redis";
    private static final String SERVICE = "redis_service";
    private static final String SCHEME = "redis://";

    /**
     * Shall run Redis in password less mode.
     */
    @Builder.Default
    protected boolean enablePassword = false;

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
    public RedisDockerService configure(@NonNull ServiceConfigureContext context) {
        var dockerRegistry = context.dockerRegistry();
        var dockerImage = DockerImageResolver.resolve(dockerRegistry, DOCKER_IMAGE, dockerImageVersion);
        var dockerCommandLineRunner = new DockerCommandLineRunner();
        var config = createConfig(dockerImage);
        var container = GenericContainerFactory.create(dockerImage);
        var configurationRegistry = new DefaultConfigurationRegistry();
        var endpointRegisterer = new EndpointRegisterer(configurationRegistry);
        var credentialsRegisterer = new RedisCredentialsRegisterer(configurationRegistry);
        var passwordConfigurer = new RedisPasswordConfigurer(dockerCommandLineRunner);

        return new RedisDockerService(
                container,
                config,
                configurationRegistry,
                endpointRegisterer,
                credentialsRegisterer,
                passwordConfigurer
        );
    }

    private RedisDockerServiceConfig createConfig(DockerImageName dockerImage) {
        var resolvedName = ServiceNameResolver.resolve(SERVICE, name);

        return RedisDockerServiceConfig
                .builder()
                .name(resolvedName)
                .dockerImage(dockerImage.asCanonicalNameString())
                .scheme(SCHEME)
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
