package pl.netroute.hussar.service.nosql.mongodb.api;

import lombok.NonNull;
import lombok.Singular;
import lombok.experimental.SuperBuilder;
import org.testcontainers.utility.DockerImageName;
import pl.netroute.hussar.core.configuration.api.DefaultConfigurationRegistry;
import pl.netroute.hussar.core.service.api.BaseDockerServiceConfigurer;
import pl.netroute.hussar.core.docker.GenericContainerFactory;
import pl.netroute.hussar.core.service.ServiceConfigureContext;
import pl.netroute.hussar.core.service.registerer.EndpointRegisterer;
import pl.netroute.hussar.core.docker.DockerImageResolver;
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
     * Set of properties to be used to register {@link pl.netroute.hussar.core.api.Endpoint} with {@link MongoDBCredentials} under.
     */
    @Singular
    protected final Set<String> registerEndpointWithCredentialsUnderProperties;

    /**
     * Set of environment variables to be used to register {@link pl.netroute.hussar.core.api.Endpoint} with {@link MongoDBCredentials} under.
     */
    @Singular
    protected final Set<String> registerEndpointWithCredentialsUnderEnvironmentVariables;

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

    public MongoDBDockerService configure(@NonNull ServiceConfigureContext context) {
        var dockerRegistry = context.dockerRegistry();
        var dockerNetwork = context.dockerNetwork();
        var networkConfigurer = context.networkConfigurer();
        var dockerImage = DockerImageResolver.resolve(dockerRegistry, DOCKER_IMAGE, dockerImageVersion);
        var config = createConfig(dockerImage);
        var container = GenericContainerFactory.create(dockerImage);
        var configurationRegistry = new DefaultConfigurationRegistry();
        var endpointRegisterer = new EndpointRegisterer(configurationRegistry);
        var endpointWithCredentialsRegisterer = new MongoDBEndpointWithCredentialsRegisterer(configurationRegistry);
        var credentialsRegisterer = new MongoDBCredentialsRegisterer(configurationRegistry);

        return new MongoDBDockerService(
                container,
                dockerNetwork,
                config,
                configurationRegistry,
                endpointRegisterer,
                networkConfigurer,
                endpointWithCredentialsRegisterer,
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
                .registerEndpointWithCredentialsUnderProperties(registerEndpointWithCredentialsUnderProperties)
                .registerEndpointWithCredentialsUnderEnvironmentVariables(registerEndpointWithCredentialsUnderEnvironmentVariables)
                .build();
    }
}
