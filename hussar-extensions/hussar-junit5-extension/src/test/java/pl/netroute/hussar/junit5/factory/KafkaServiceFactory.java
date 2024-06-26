package pl.netroute.hussar.junit5.factory;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.netroute.hussar.service.kafka.KafkaDockerService;
import pl.netroute.hussar.service.kafka.KafkaDockerServiceConfigurer;
import pl.netroute.hussar.service.kafka.api.KafkaTopic;

import static pl.netroute.hussar.junit5.config.ApplicationEnvironmentVariables.KAFKA_URL_ENV_VARIABLE;
import static pl.netroute.hussar.junit5.config.ApplicationProperties.KAFKA_ALTERNATIVE_URL_PROPERTY;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class KafkaServiceFactory {
    public static final String KAFKA_NAME = "kafka-instance";

    public static final KafkaTopic KAFKA_EVENTS_TOPIC = KafkaTopic
            .builder()
            .name("hussar.events.stream")
            .partitions(5)
            .build();

    public static KafkaDockerService create() {
        var dockerImageVersion = "7.5.4";

        return KafkaDockerServiceConfigurer
                .newInstance()
                .name(KAFKA_NAME)
                .dockerImageVersion(dockerImageVersion)
                .kraftMode(true)
                .topicAutoCreation(true)
                .topic(KAFKA_EVENTS_TOPIC)
                .registerEndpointUnderProperty(KAFKA_ALTERNATIVE_URL_PROPERTY)
                .registerEndpointUnderEnvironmentVariable(KAFKA_URL_ENV_VARIABLE)
                .done()
                .configure();
    }

}
