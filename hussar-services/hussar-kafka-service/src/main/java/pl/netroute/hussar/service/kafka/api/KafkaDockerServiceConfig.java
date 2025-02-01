package pl.netroute.hussar.service.kafka.api;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.SuperBuilder;
import pl.netroute.hussar.core.api.InternalUseOnly;
import pl.netroute.hussar.core.service.api.BaseDockerServiceConfig;

import java.util.Set;

@Value
@SuperBuilder
@InternalUseOnly
@EqualsAndHashCode(callSuper = true)
class KafkaDockerServiceConfig extends BaseDockerServiceConfig {
    boolean kraftMode;
    boolean topicAutoCreation;

    @NonNull
    Set<KafkaTopic> topics;
}
