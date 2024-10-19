package pl.netroute.hussar.service.kafka;

import lombok.NonNull;
import org.apache.kafka.clients.admin.AdminClient;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.KafkaContainer;
import pl.netroute.hussar.core.api.Endpoint;
import pl.netroute.hussar.core.api.configuration.ConfigurationRegistry;
import pl.netroute.hussar.core.api.service.Service;
import pl.netroute.hussar.core.api.service.ServiceStartupContext;
import pl.netroute.hussar.core.helper.EndpointHelper;
import pl.netroute.hussar.core.service.BaseDockerService;
import pl.netroute.hussar.core.service.registerer.EndpointRegisterer;

import java.util.List;

/**
 * Hussar Docker {@link Service} representing Kafka.
 */
public class KafkaDockerService extends BaseDockerService<KafkaDockerServiceConfig> {
    private final KafkaListenerConfigurer listenerConfigurer;
    private final KafkaTopicConfigurer topicConfigurer;
    private final KafkaTopicAutoCreationConfigurer topicAutoCreationConfigurer;
    private final KafkaKraftModeConfigurer kraftModeConfigurer;

    /**
     * Creates new instance of {@link KafkaDockerService}.
     *
     * @param container - the {@link KafkaContainer} used by this {@link KafkaDockerService}.
     * @param config - the {@link KafkaDockerServiceConfig} used by this {@link KafkaDockerService}.
     * @param configurationRegistry - the {@link ConfigurationRegistry} used by this {@link KafkaDockerService}.
     * @param endpointRegisterer - the  {@link EndpointRegisterer} used by this {@link KafkaDockerService}.
     * @param listenerConfigurer - the {@link KafkaListenerConfigurer} used by this {@link KafkaDockerService}.
     * @param topicConfigurer - the {@link KafkaTopicConfigurer} used by this {@link KafkaDockerService}.
     * @param topicAutoCreationConfigurer - the {@link KafkaTopicConfigurer} used by this {@link KafkaDockerService}.
     * @param kraftModeConfigurer - the {@link KafkaKraftModeConfigurer} used by this {@link KafkaDockerService}.
     */
    KafkaDockerService(@NonNull KafkaContainer container,
                       @NonNull KafkaDockerServiceConfig config,
                       @NonNull ConfigurationRegistry configurationRegistry,
                       @NonNull EndpointRegisterer endpointRegisterer,
                       @NonNull KafkaListenerConfigurer listenerConfigurer,
                       @NonNull KafkaTopicConfigurer topicConfigurer,
                       @NonNull KafkaTopicAutoCreationConfigurer topicAutoCreationConfigurer,
                       @NonNull KafkaKraftModeConfigurer kraftModeConfigurer) {
        super(container, config, configurationRegistry, endpointRegisterer);

        this.listenerConfigurer = listenerConfigurer;
        this.topicConfigurer = topicConfigurer;
        this.topicAutoCreationConfigurer = topicAutoCreationConfigurer;
        this.kraftModeConfigurer = kraftModeConfigurer;
    }

    @Override
    public List<Endpoint> getEndpoints() {
        var externalListener = KafkaListenerConfigurer.EXTERNAL_LISTENER;

        var host = container.getHost();
        var port = container.getMappedPort(externalListener.port());
        var endpoint = Endpoint.schemeLess(host, port);

        return List.of(endpoint);
    }

    @Override
    protected void configureContainer(GenericContainer<?> container) {
        super.configureContainer(container);

        var kafkaContainer = (KafkaContainer) container;
        listenerConfigurer.configure(kafkaContainer);
        topicAutoCreationConfigurer.configure(config.isTopicAutoCreation(), kafkaContainer);

        configureKraftMode(kafkaContainer);

        kafkaContainer.withExposedPorts(KafkaListenerConfigurer.EXTERNAL_LISTENER.port());
    }

    @Override
    protected void doAfterServiceStartup(ServiceStartupContext context) {
        super.doAfterServiceStartup(context);

        var endpoint = EndpointHelper.getAnyEndpointOrFail(this);
        var adminClient = KafkaAdminClientFactory.create(endpoint);

        configureTopics(adminClient);
    }

    private void configureKraftMode(KafkaContainer container) {
        if(config.isKraftMode()) {
            kraftModeConfigurer.configure(container);
        }
    }

    private void configureTopics(AdminClient adminClient) {
        var topics = config.getTopics();

        topics.forEach(topic -> topicConfigurer.configure(adminClient, topic));
    }

}
