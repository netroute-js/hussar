package pl.netroute.hussar.service.nosql.mongodb;

import lombok.Singular;
import lombok.experimental.SuperBuilder;
import org.testcontainers.utility.DockerImageName;
import pl.netroute.hussar.core.configuration.api.DefaultConfigurationRegistry;
import pl.netroute.hussar.core.service.BaseDockerServiceConfigurer;
import pl.netroute.hussar.core.service.container.GenericContainerFactory;
import pl.netroute.hussar.core.service.registerer.EndpointRegisterer;
import pl.netroute.hussar.core.service.resolver.DockerImageResolver;
import pl.netroute.hussar.core.service.resolver.ServiceNameResolver;

import java.util.Set;

/**
 * Hussar {@link MongoDBDockerService} configurer. This is the only way to create {@link MongoDBDockerService}.
 */
@SuperBuilder(builderMethodName = "newInstance", buildMethodName = "done")
public class MongoDBDockerServiceConfigurer extends BaseDockerServiceConfigurer<MongoDBDockerService> {
    private static final String DOCKER_IMAGE = "mongo";
    private static final String SERVICE = "mongodb_service";
    private static final String MONGODB_SCHEME = "mongodb://";

    /**
     * Set of properties to be used to register MongoDB username under.
     */
    @Singular
    protected final Set<String> registerUsernameUnderProperties;

    /**
     * Set of environment variables to be used to register MongoDB username under.
     */
    @Singular
    protected final Set<String> registerUsernameUnderEnvironmentVariables;

    /**
     * Set of properties to be used to register MongoDB password under.
     */
    @Singular
    protected final Set<String> registerPasswordUnderProperties;

    /**
     * Set of environment variables to be used to register MongoDB password under.
     */
    @Singular
    protected final Set<String> registerPasswordUnderEnvironmentVariables;

    public MongoDBDockerService configure() {
        var dockerImage = DockerImageResolver.resolve(dockerRegistryUrl, DOCKER_IMAGE, dockerImageVersion);
        var config = createConfig(dockerImage);
        var container = GenericContainerFactory.create(dockerImage);
        var configurationRegistry = new DefaultConfigurationRegistry();
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

    private MongoDBDockerServiceConfig createConfig(DockerImageName dockerImage) {
        var resolvedName = ServiceNameResolver.resolve(SERVICE, name);

        return MongoDBDockerServiceConfig
                .builder()
                .name(resolvedName)
                .dockerImage(dockerImage.asCanonicalNameString())
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
