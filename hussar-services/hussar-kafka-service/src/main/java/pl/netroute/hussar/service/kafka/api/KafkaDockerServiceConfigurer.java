package pl.netroute.hussar.service.kafka.api;

import lombok.NonNull;
import lombok.Singular;
import lombok.experimental.SuperBuilder;
import org.testcontainers.kafka.ConfluentKafkaContainer;
import org.testcontainers.utility.DockerImageName;
import pl.netroute.hussar.core.configuration.api.DefaultConfigurationRegistry;
import pl.netroute.hussar.core.docker.DockerImageResolver;
import pl.netroute.hussar.core.docker.DockerRegistryResolver;
import pl.netroute.hussar.core.service.ServiceConfigureContext;
import pl.netroute.hussar.core.service.api.BaseDockerServiceConfigurer;
import pl.netroute.hussar.core.service.registerer.EndpointRegisterer;
import pl.netroute.hussar.core.service.resolver.ServiceNameResolver;

import java.util.Set;

/**
 * Hussar {@link KafkaDockerService} configurer. This is the only way to create {@link KafkaDockerService}.
 */
@SuperBuilder(builderMethodName = "newInstance", buildMethodName = "done")
public class KafkaDockerServiceConfigurer extends BaseDockerServiceConfigurer<KafkaDockerService> {
    private static final String DOCKER_IMAGE = "confluentinc/cp-kafka";
    private static final String SERVICE = "kafka_service";
    private static final String KAFKA_SCHEME = "";

    /**
     * Shall configure auto topic creation.
     */
    boolean topicAutoCreation;

    /**
     * Set of topics to be created on Kafka startup.
     */
    @Singular
    protected final Set<KafkaTopic> topics;

    @Override
    public KafkaDockerService configure(@NonNull ServiceConfigureContext context) {
        var resolvedDockerRegistry = DockerRegistryResolver.resolve(dockerRegistry, context);
        var dockerNetwork = context.dockerNetwork();
        var networkConfigurer = context.networkConfigurer();
        var dockerImage = DockerImageResolver.resolve(resolvedDockerRegistry, DOCKER_IMAGE, dockerImageVersion);
        var config = createConfig(dockerImage);
        var container = createContainer(dockerImage);
        var configurationRegistry = new DefaultConfigurationRegistry();
        var endpointRegisterer = new EndpointRegisterer(configurationRegistry);
        var topicConfigurer = new KafkaTopicConfigurer();
        var topicAutoCreationConfigurer = new KafkaTopicAutoCreationConfigurer();

        return new KafkaDockerService(
                container,
                dockerNetwork,
                config,
                configurationRegistry,
                endpointRegisterer,
                networkConfigurer,
                topicConfigurer,
                topicAutoCreationConfigurer
        );
    }

    private ConfluentKafkaContainer createContainer(DockerImageName dockerImage) {
        return new ConfluentKafkaContainer(dockerImage);
    }

    private KafkaDockerServiceConfig createConfig(DockerImageName dockerImage) {
        var resolvedName = ServiceNameResolver.resolve(SERVICE, name);

        return KafkaDockerServiceConfig
                .builder()
                .name(resolvedName)
                .dockerImage(dockerImage.asCanonicalNameString())
                .startupTimeout(startupTimeout)
                .scheme(KAFKA_SCHEME)
                .topics(topics)
                .topicAutoCreation(topicAutoCreation)
                .registerEndpointUnderProperties(registerEndpointUnderProperties)
                .registerEndpointUnderEnvironmentVariables(registerEndpointUnderEnvironmentVariables)
                .build();
    }

}
