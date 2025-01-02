package pl.netroute.hussar.service.rabbitmq;

import lombok.Singular;
import lombok.experimental.SuperBuilder;
import org.testcontainers.utility.DockerImageName;
import pl.netroute.hussar.core.configuration.api.DefaultConfigurationRegistry;
import pl.netroute.hussar.core.helper.SchemesHelper;
import pl.netroute.hussar.core.service.BaseDockerServiceConfigurer;
import pl.netroute.hussar.core.service.container.GenericContainerFactory;
import pl.netroute.hussar.core.service.registerer.EndpointRegisterer;
import pl.netroute.hussar.core.service.resolver.DockerImageResolver;
import pl.netroute.hussar.core.service.resolver.ServiceNameResolver;
import pl.netroute.hussar.service.rabbitmq.api.RabbitMQQueue;

import java.util.Set;

/**
 * Hussar {@link RabbitMQDockerService} configurer. This is the only way to create {@link RabbitMQDockerService}.
 */
@SuperBuilder(builderMethodName = "newInstance", buildMethodName = "done")
public class RabbitMQDockerServiceConfigurer extends BaseDockerServiceConfigurer<RabbitMQDockerService> {
    private static final String DOCKER_IMAGE = "rabbitmq";
    private static final String SERVICE = "rabbitmq_service";

    /**
     * Set of queues to be created on RabbitMQ startup.
     */
    @Singular
    protected final Set<RabbitMQQueue> queues;

    /**
     * Set of properties to be used to register RabbitMQ username under.
     */
    @Singular
    protected final Set<String> registerUsernameUnderProperties;

    /**
     * Set of environment variables to be used to register RabbitMQ username under.
     */
    @Singular
    protected final Set<String> registerUsernameUnderEnvironmentVariables;

    /**
     * Set of properties to be used to register RabbitMQ password under.
     */
    @Singular
    protected final Set<String> registerPasswordUnderProperties;

    /**
     * Set of environment variables to be used to register RabbitMQ password under.
     */
    @Singular
    protected final Set<String> registerPasswordUnderEnvironmentVariables;

    @Override
    public RabbitMQDockerService configure() {
        var dockerImage = DockerImageResolver.resolve(dockerRegistryUrl, DOCKER_IMAGE, dockerImageVersion);
        var config = createConfig(dockerImage);
        var container = GenericContainerFactory.create(dockerImage);
        var configurationRegistry = new DefaultConfigurationRegistry();
        var endpointRegisterer = new EndpointRegisterer(configurationRegistry);
        var credentialsRegisterer = new RabbitMQCredentialsRegisterer(configurationRegistry);
        var queueConfigurer = new RabbitMQQueueConfigurer();

        return new RabbitMQDockerService(
                container,
                config,
                configurationRegistry,
                endpointRegisterer,
                credentialsRegisterer,
                queueConfigurer
        );
    }

    private RabbitMQDockerServiceConfig createConfig(DockerImageName dockerImage) {
        var resolvedName = ServiceNameResolver.resolve(SERVICE, name);

        return RabbitMQDockerServiceConfig
                .builder()
                .name(resolvedName)
                .dockerImage(dockerImage.asCanonicalNameString())
                .scheme(SchemesHelper.EMPTY_SCHEME)
                .queues(queues)
                .registerUsernameUnderProperties(registerUsernameUnderProperties)
                .registerUsernameUnderEnvironmentVariables(registerUsernameUnderEnvironmentVariables)
                .registerPasswordUnderProperties(registerPasswordUnderProperties)
                .registerPasswordUnderEnvironmentVariables(registerPasswordUnderEnvironmentVariables)
                .registerEndpointUnderProperties(registerEndpointUnderProperties)
                .registerEndpointUnderEnvironmentVariables(registerEndpointUnderEnvironmentVariables)
                .build();
    }
}
