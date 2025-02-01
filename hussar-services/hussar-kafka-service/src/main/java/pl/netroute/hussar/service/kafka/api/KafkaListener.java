package pl.netroute.hussar.service.kafka.api;

import lombok.NonNull;
import pl.netroute.hussar.core.api.InternalUseOnly;

@InternalUseOnly
record KafkaListener(@NonNull String name,
                     @NonNull String host,
                     @NonNull String protocol,
                     int port) {
    private static final String LISTENER_TEMPLATE = "%s://%s:%d";
    private static final String LISTENER_PROTOCOL_TEMPLATE = "%s:%s";

    String configuredListener() {
        return LISTENER_TEMPLATE.formatted(name, host, port);
    }

    String configuredProtocol() {
        return LISTENER_PROTOCOL_TEMPLATE.formatted(name, protocol);
    }

    static KafkaListener newListener(@NonNull KafkaListener listener,
                                     @NonNull String host,
                                     int port) {
        var name = listener.name();
        var protocol = listener.protocol();

        return new KafkaListener(name, host, protocol, port);
    }

}
