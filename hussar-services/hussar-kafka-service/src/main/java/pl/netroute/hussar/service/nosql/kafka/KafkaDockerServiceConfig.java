package pl.netroute.hussar.service.nosql.kafka;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.SuperBuilder;
import pl.netroute.hussar.core.service.BaseDockerServiceConfig;
import pl.netroute.hussar.service.nosql.kafka.api.Topic;

import java.util.Set;

@Value
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class KafkaDockerServiceConfig extends BaseDockerServiceConfig {
    boolean kraftMode;
    boolean topicAutoCreation;

    @NonNull
    Set<Topic> topics;
}
