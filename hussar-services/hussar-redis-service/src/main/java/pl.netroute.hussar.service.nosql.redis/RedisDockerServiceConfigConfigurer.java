package pl.netroute.hussar.service.nosql.redis;

import lombok.Builder;
import lombok.Singular;
import lombok.experimental.SuperBuilder;
import pl.netroute.hussar.core.api.MapConfigurationRegistry;
import pl.netroute.hussar.core.service.BaseDockerServiceConfigurer;
import pl.netroute.hussar.core.service.container.GenericContainerFactory;
import pl.netroute.hussar.core.service.registerer.EndpointRegisterer;
import pl.netroute.hussar.core.service.resolver.DockerImageResolver;
import pl.netroute.hussar.core.service.resolver.ServiceNameResolver;

import java.util.Set;

@SuperBuilder(builderMethodName = "newInstance", buildMethodName = "done")
class RedisDockerServiceConfigConfigurer extends BaseDockerServiceConfigurer<RedisDockerService> {
    private static final String DOCKER_IMAGE = "redis";
    private static final String SERVICE = "redis_service";
    private static final String REDIS_SCHEME = "redis://";

    @Builder.Default boolean enablePassword = false;

    @Singular
    protected final Set<String> registerUsernameUnderProperties;

    @Singular
    protected final Set<String> registerUsernameUnderEnvironmentVariables;

    @Singular
    protected final Set<String> registerPasswordUnderProperties;

    @Singular
    protected final Set<String> registerPasswordUnderEnvironmentVariables;

    public RedisDockerService configure() {
        var config = createConfig();
        var container = GenericContainerFactory.create(config);
        var configurationRegistry = new MapConfigurationRegistry();
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
