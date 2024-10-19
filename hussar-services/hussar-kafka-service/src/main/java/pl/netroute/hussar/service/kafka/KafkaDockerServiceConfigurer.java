package pl.netroute.hussar.service.kafka;

import lombok.Singular;
import lombok.experimental.SuperBuilder;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;
import pl.netroute.hussar.core.api.MapConfigurationRegistry;
import pl.netroute.hussar.core.service.BaseDockerServiceConfigurer;
import pl.netroute.hussar.core.service.registerer.EndpointRegisterer;
import pl.netroute.hussar.core.service.resolver.DockerImageResolver;
import pl.netroute.hussar.core.service.resolver.ServiceNameResolver;
import pl.netroute.hussar.service.kafka.api.KafkaTopic;

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
    public KafkaDockerService configure() {
        var config = createConfig();
        var container = createContainer(config);
        var configurationRegistry = new MapConfigurationRegistry();
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
                listenerConfigurer,
                topicConfigurer,
                topicAutoCreationConfigurer,
                kraftModeConfigurer
        );
    }

    private KafkaContainer createContainer(KafkaDockerServiceConfig config) {
        var dockerImage = DockerImageName.parse(config.getDockerImage());

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

    private KafkaDockerServiceConfig createConfig() {
        var resolvedName = ServiceNameResolver.resolve(SERVICE, name);
        var resolvedDockerImage = DockerImageResolver.resolve(DOCKER_IMAGE, dockerImageVersion);

        return KafkaDockerServiceConfig
                .builder()
                .name(resolvedName)
                .dockerImage(resolvedDockerImage)
                .scheme(KAFKA_SCHEME)
                .topics(topics)
                .kraftMode(kraftMode)
                .topicAutoCreation(topicAutoCreation)
                .registerEndpointUnderProperties(registerEndpointUnderProperties)
                .registerEndpointUnderEnvironmentVariables(registerEndpointUnderEnvironmentVariables)
                .build();
    }

}
