package pl.netroute.hussar.service.kafka.api;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.testcontainers.containers.KafkaContainer;
import pl.netroute.hussar.core.api.InternalUseOnly;

@Slf4j
@InternalUseOnly
@NoArgsConstructor(access = AccessLevel.PACKAGE)
class KafkaTopicAutoCreationConfigurer {
    private static final String KAFKA_AUTO_CREATE_TOPICS_ENABLE_ENV = "KAFKA_AUTO_CREATE_TOPICS_ENABLE";

    void configure(boolean autoTopicCreation, @NonNull KafkaContainer container) {
        log.info("Configuring Topic auto creation - {}", autoTopicCreation);

        container.withEnv(KAFKA_AUTO_CREATE_TOPICS_ENABLE_ENV, autoTopicCreation + "");
    }

}
