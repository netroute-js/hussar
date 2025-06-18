package pl.netroute.hussar.service.kafka.api;

import lombok.NonNull;
import org.apache.kafka.clients.admin.AdminClient;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.kafka.ConfluentKafkaContainer;
import pl.netroute.hussar.core.configuration.api.ConfigurationRegistry;
import pl.netroute.hussar.core.docker.api.DockerNetwork;
import pl.netroute.hussar.core.helper.EndpointHelper;
import pl.netroute.hussar.core.network.api.NetworkConfigurer;
import pl.netroute.hussar.core.service.ServiceStartupContext;
import pl.netroute.hussar.core.service.api.BaseDockerService;
import pl.netroute.hussar.core.service.api.Service;
import pl.netroute.hussar.core.service.registerer.EndpointRegisterer;

import java.util.List;

/**
 * Hussar Docker {@link Service} representing Kafka.
 */
public class KafkaDockerService extends BaseDockerService<KafkaDockerServiceConfig> {
    private static final int KAFKA_LISTENING_PORT = 9092;

    @NonNull
    private final KafkaTopicConfigurer topicConfigurer;

    @NonNull
    private final KafkaTopicAutoCreationConfigurer topicAutoCreationConfigurer;

    /**
     * Creates new instance of {@link KafkaDockerService}.
     *
     * @param container - the {@link ConfluentKafkaContainer} used by this {@link KafkaDockerService}.
     * @param dockerNetwork - the {@link DockerNetwork} used by this {@link KafkaDockerService}.
     * @param config - the {@link KafkaDockerServiceConfig} used by this {@link KafkaDockerService}.
     * @param configurationRegistry - the {@link ConfigurationRegistry} used by this {@link KafkaDockerService}.
     * @param endpointRegisterer - the  {@link EndpointRegisterer} used by this {@link KafkaDockerService}.
     * @param networkConfigurer - the  {@link NetworkConfigurer} used by this {@link KafkaDockerService}.
     * @param topicConfigurer - the {@link KafkaTopicConfigurer} used by this {@link KafkaDockerService}.
     * @param topicAutoCreationConfigurer - the {@link KafkaTopicConfigurer} used by this {@link KafkaDockerService}.
     */
    KafkaDockerService(@NonNull ConfluentKafkaContainer container,
                       @NonNull DockerNetwork dockerNetwork,
                       @NonNull KafkaDockerServiceConfig config,
                       @NonNull ConfigurationRegistry configurationRegistry,
                       @NonNull EndpointRegisterer endpointRegisterer,
                       @NonNull NetworkConfigurer networkConfigurer,
                       @NonNull KafkaTopicConfigurer topicConfigurer,
                       @NonNull KafkaTopicAutoCreationConfigurer topicAutoCreationConfigurer) {
        super(container, dockerNetwork, config, configurationRegistry, endpointRegisterer, networkConfigurer);

        this.topicConfigurer = topicConfigurer;
        this.topicAutoCreationConfigurer = topicAutoCreationConfigurer;
    }

    @Override
    protected List<Integer> getInternalPorts() {
        return List.of(KAFKA_LISTENING_PORT);
    }

    @Override
    protected void configureContainer(GenericContainer<?> container) {
        super.configureContainer(container);

        var kafkaContainer = (ConfluentKafkaContainer) container;
        topicAutoCreationConfigurer.configure(config.isTopicAutoCreation(), kafkaContainer);
    }

    @Override
    protected void doAfterServiceStartup(ServiceStartupContext context) {
        super.doAfterServiceStartup(context);

        var endpoint = EndpointHelper.getAnyEndpointOrFail(this);

        try(var adminClient = KafkaAdminClientFactory.create(endpoint)) {
            configureTopics(adminClient);
        }
    }

    private void configureTopics(AdminClient adminClient) {
        var topics = config.getTopics();

        topics.forEach(topic -> topicConfigurer.configure(adminClient, topic));
    }

}
