package pl.netroute.hussar.service.kafka.helper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import pl.netroute.hussar.core.helper.EndpointHelper;
import pl.netroute.hussar.service.kafka.KafkaDockerService;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class KafkaSenderFactory {
    private static final long MAX_BLOCK_MS = 5000;

    public static KafkaSender create(@NonNull KafkaDockerService kafkaService) {
        var endpoint = EndpointHelper.getAnyEndpointOrFail(kafkaService);

        var connectionProperties = Map.<String, Object>of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, endpoint.address(),
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
                ProducerConfig.MAX_BLOCK_MS_CONFIG, MAX_BLOCK_MS
        );

        var producer = new KafkaProducer<String, String>(connectionProperties);

        return new KafkaSender(producer);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class KafkaSender {
        private static final Duration SEND_TIMEOUT = Duration.ofSeconds(5L);

        private final KafkaProducer<String, String> producer;

        public void send(@NonNull String topic, @NonNull String value) {
            var record = new ProducerRecord<String, String>(topic, value);

            try {
                producer
                        .send(record)
                        .get(SEND_TIMEOUT.toSeconds(), TimeUnit.SECONDS);
            } catch (Exception ex) {
                throw new IllegalStateException("Could not send record", ex);
            }
        }

    }

}
