package pl.netroute.hussar.service.kafka;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.testcontainers.containers.KafkaContainer;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PACKAGE)
class KafkaKraftModeConfigurer {

    void configure(@NonNull KafkaContainer container) {
        log.info("Configuring Kafka Kraft mode");

        container.withKraft();
    }

}
