package pl.netroute.hussar.service.rabbitmq;

import lombok.Singular;
import lombok.experimental.SuperBuilder;
import pl.netroute.hussar.core.api.MapConfigurationRegistry;
import pl.netroute.hussar.core.helper.SchemesHelper;
import pl.netroute.hussar.core.service.BaseDockerServiceConfigurer;
import pl.netroute.hussar.core.service.container.GenericContainerFactory;
import pl.netroute.hussar.core.service.registerer.EndpointRegisterer;
import pl.netroute.hussar.core.service.resolver.DockerImageResolver;
import pl.netroute.hussar.core.service.resolver.ServiceNameResolver;
import pl.netroute.hussar.service.rabbitmq.api.RabbitMQQueue;

import java.util.Set;

@SuperBuilder(builderMethodName = "newInstance", buildMethodName = "done")
public class RabbitMQDockerServiceConfigurer extends BaseDockerServiceConfigurer<RabbitMQDockerService> {
    private static final String DOCKER_IMAGE = "rabbitmq";
    private static final String SERVICE = "rabbitmq_service";

    @Singular
    protected final Set<RabbitMQQueue> queues;

    @Singular
    protected final Set<String> registerUsernameUnderProperties;

    @Singular
    protected final Set<String> registerUsernameUnderEnvironmentVariables;

    @Singular
    protected final Set<String> registerPasswordUnderProperties;

    @Singular
    protected final Set<String> registerPasswordUnderEnvironmentVariables;

    @Override
    protected RabbitMQDockerService configure() {
        var config = createConfig();
        var container = GenericContainerFactory.create(config);
        var configurationRegistry = new MapConfigurationRegistry();
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

    private RabbitMQDockerServiceConfig createConfig() {
        var resolvedName = ServiceNameResolver.resolve(SERVICE, name);
        var resolvedDockerImage = DockerImageResolver.resolve(DOCKER_IMAGE, dockerImageVersion);

        return RabbitMQDockerServiceConfig
                .builder()
                .name(resolvedName)
                .dockerImage(resolvedDockerImage)
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
