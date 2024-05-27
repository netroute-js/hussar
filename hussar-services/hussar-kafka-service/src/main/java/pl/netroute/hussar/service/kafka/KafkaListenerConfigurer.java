package pl.netroute.hussar.service.kafka;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.testcontainers.containers.KafkaContainer;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
class KafkaListenerConfigurer {
    private static final int INTERNAL_LISTENER_PORT = 9092;
    private static final int EXTERNAL_LISTENER_PORT = KafkaContainer.KAFKA_PORT;

    private static final String INTERNAL_LISTENER_NAME = "BROKER";
    private static final String EXTERNAL_LISTENER_NAME = "EXTERNAL";

    private static final String ALL_INTERFACES = "0.0.0.0";
    private static final String PLAINTEXT_PROTOCOL = "PLAINTEXT";

    private static final String KAFKA_LISTENERS_ENV = "KAFKA_LISTENERS";
    private static final String KAFKA_LISTENER_SECURITY_PROTOCOL_MAP_ENV = "KAFKA_LISTENER_SECURITY_PROTOCOL_MAP";
    private static final String KAFKA_INTER_BROKER_LISTENER_NAME_ENV = "KAFKA_INTER_BROKER_LISTENER_NAME";

    private static final String DELIMITER = ",";

    static final KafkaListener INTERNAL_LISTENER = new KafkaListener(INTERNAL_LISTENER_NAME, ALL_INTERFACES, PLAINTEXT_PROTOCOL, INTERNAL_LISTENER_PORT);
    static final KafkaListener EXTERNAL_LISTENER = new KafkaListener(EXTERNAL_LISTENER_NAME, ALL_INTERFACES, PLAINTEXT_PROTOCOL, EXTERNAL_LISTENER_PORT);

    void configure(@NonNull KafkaContainer container) {
        var listeners = resolveListeners();
        var protocolMap = resolveProtocolMap();

        container.withEnv(KAFKA_LISTENERS_ENV, listeners);
        container.withEnv(KAFKA_LISTENER_SECURITY_PROTOCOL_MAP_ENV, protocolMap);
        container.withEnv(KAFKA_INTER_BROKER_LISTENER_NAME_ENV, INTERNAL_LISTENER_NAME);
    }

    private String resolveListeners() {
        return Stream
                .of(INTERNAL_LISTENER, EXTERNAL_LISTENER)
                .map(KafkaListener::configuredListener)
                .collect(Collectors.joining(DELIMITER));
    }

    private String resolveProtocolMap() {
        return Stream
                .of(INTERNAL_LISTENER, EXTERNAL_LISTENER)
                .map(KafkaListener::configuredProtocol)
                .collect(Collectors.joining(DELIMITER));
    }

}
