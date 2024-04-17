package pl.netroute.hussar.service.nosql.mongodb;

import lombok.Singular;
import lombok.experimental.SuperBuilder;
import pl.netroute.hussar.core.api.MapConfigurationRegistry;
import pl.netroute.hussar.core.service.BaseDockerServiceConfigurer;
import pl.netroute.hussar.core.service.container.GenericContainerFactory;
import pl.netroute.hussar.core.service.registerer.EndpointRegisterer;
import pl.netroute.hussar.core.service.resolver.DockerImageResolver;
import pl.netroute.hussar.core.service.resolver.ServiceNameResolver;
import pl.netroute.hussar.service.nosql.mongodb.registerer.MongoDBCredentialsRegisterer;

import java.util.Set;

@SuperBuilder(builderMethodName = "newInstance", buildMethodName = "done")
public class MongoDBDockerServiceConfigurer extends BaseDockerServiceConfigurer<MongoDBDockerService> {
    private static final String DOCKER_IMAGE = "mongo";
    private static final String SERVICE = "mongodb_service";
    private static final String MONGODB_SCHEME = "mongodb://";

    @Singular
    protected final Set<String> registerUsernameUnderProperties;

    @Singular
    protected final Set<String> registerUsernameUnderEnvironmentVariables;

    @Singular
    protected final Set<String> registerPasswordUnderProperties;

    @Singular
    protected final Set<String> registerPasswordUnderEnvironmentVariables;

    public MongoDBDockerService configure() {
        var config = createConfig();
        var container = GenericContainerFactory.create(config);
        var configurationRegistry = new MapConfigurationRegistry();
        var endpointRegisterer = new EndpointRegisterer(configurationRegistry);
        var credentialsRegisterer = new MongoDBCredentialsRegisterer(configurationRegistry);

        return new MongoDBDockerService(
                container,
                config,
                configurationRegistry,
                endpointRegisterer,
                credentialsRegisterer
        );
    }

    private MongoDBDockerServiceConfig createConfig() {
        var resolvedName = ServiceNameResolver.resolve(SERVICE, name);
        var resolvedDockerImage = DockerImageResolver.resolve(DOCKER_IMAGE, dockerImageVersion);

        return MongoDBDockerServiceConfig
                .builder()
                .name(resolvedName)
                .dockerImage(resolvedDockerImage)
                .scheme(MONGODB_SCHEME)
                .registerUsernameUnderProperties(registerUsernameUnderProperties)
                .registerUsernameUnderEnvironmentVariables(registerUsernameUnderEnvironmentVariables)
                .registerPasswordUnderProperties(registerPasswordUnderProperties)
                .registerPasswordUnderEnvironmentVariables(registerPasswordUnderEnvironmentVariables)
                .registerEndpointUnderProperties(registerEndpointUnderProperties)
                .registerEndpointUnderEnvironmentVariables(registerEndpointUnderEnvironmentVariables)
                .build();
    }
}
