package pl.netroute.hussar.service.kafka.api;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.producer.ProducerConfig;
import pl.netroute.hussar.core.api.Endpoint;
import pl.netroute.hussar.core.api.InternalUseOnly;

import java.util.Map;

@InternalUseOnly
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class KafkaAdminClientFactory {

    static AdminClient create(@NonNull Endpoint endpoint) {
        var connectionProperties = Map.<String, Object>of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, endpoint.address()
        );

        return AdminClient.create(connectionProperties);
    }

}
