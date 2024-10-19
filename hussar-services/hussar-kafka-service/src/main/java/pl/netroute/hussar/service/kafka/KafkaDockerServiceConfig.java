package pl.netroute.hussar.service.kafka;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.SuperBuilder;
import pl.netroute.hussar.core.service.BaseDockerServiceConfig;
import pl.netroute.hussar.service.kafka.api.KafkaTopic;

import java.util.Set;

@Value
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
class KafkaDockerServiceConfig extends BaseDockerServiceConfig {
    boolean kraftMode;
    boolean topicAutoCreation;

    @NonNull
    Set<KafkaTopic> topics;
}
