package pl.netroute.hussar.service.kafka.api;

import lombok.NonNull;
import lombok.Singular;
import lombok.experimental.SuperBuilder;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;
import pl.netroute.hussar.core.configuration.api.DefaultConfigurationRegistry;
import pl.netroute.hussar.core.service.api.BaseDockerServiceConfigurer;
import pl.netroute.hussar.core.service.ServiceConfigureContext;
import pl.netroute.hussar.core.service.registerer.EndpointRegisterer;
import pl.netroute.hussar.core.docker.DockerImageResolver;
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
     * Shall configure Kafka to run in kraft mode.
     */
    boolean kraftMode;

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
        var dockerRegistry = context.dockerRegistry();
        var networkConfigurer = context.networkConfigurer();
        var dockerImage = DockerImageResolver.resolve(dockerRegistry, DOCKER_IMAGE, dockerImageVersion);
        var config = createConfig(dockerImage);
        var container = createContainer(dockerImage);
        var configurationRegistry = new DefaultConfigurationRegistry();
        var endpointRegisterer = new EndpointRegisterer(configurationRegistry);
        var listenerConfigurer = new KafkaListenerConfigurer();
        var topicConfigurer = new KafkaTopicConfigurer();
        var topicAutoCreationConfigurer = new KafkaTopicAutoCreationConfigurer();
        var kraftModeConfigurer = new KafkaKraftModeConfigurer();

        return new KafkaDockerService(
                container,
                config,
                configurationRegistry,
                endpointRegisterer,
                networkConfigurer,
                listenerConfigurer,
                topicConfigurer,
                topicAutoCreationConfigurer,
                kraftModeConfigurer
        );
    }

    private KafkaContainer createContainer(DockerImageName dockerImage) {
        return new KafkaContainer(dockerImage) {

            @Override
            public String getBootstrapServers() {
                var externalListener = KafkaListenerConfigurer.EXTERNAL_LISTENER;

                var host = getHost();
                var port = getMappedPort(externalListener.port());

                return KafkaListener
                        .newListener(externalListener, host, port)
                        .configuredListener();
            }

        };
    }

    private KafkaDockerServiceConfig createConfig(DockerImageName dockerImage) {
        var resolvedName = ServiceNameResolver.resolve(SERVICE, name);

        return KafkaDockerServiceConfig
                .builder()
                .name(resolvedName)
                .dockerImage(dockerImage.asCanonicalNameString())
                .scheme(KAFKA_SCHEME)
                .topics(topics)
                .kraftMode(kraftMode)
                .topicAutoCreation(topicAutoCreation)
                .registerEndpointUnderProperties(registerEndpointUnderProperties)
                .registerEndpointUnderEnvironmentVariables(registerEndpointUnderEnvironmentVariables)
                .build();
    }

}
