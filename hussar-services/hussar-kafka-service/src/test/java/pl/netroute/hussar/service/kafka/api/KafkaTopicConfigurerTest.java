package pl.netroute.hussar.service.kafka.api;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class KafkaTopicConfigurerTest {
    private static final int TOPIC_PARTITION = 10;
    private static final short TOPIC_REPLICATION = 1;
    private static final String TOPIC_NAME = "TopicA";

    private KafkaTopicConfigurer topicConfigurer;

    @BeforeEach
    public void setup() {
        topicConfigurer = new KafkaTopicConfigurer();
    }

    @Test
    public void shouldConfigureTopics() {
        // given
        var topic = new KafkaTopic(TOPIC_NAME, TOPIC_PARTITION);

        var adminClient = createStubAdminClient();

        // when
        topicConfigurer.configure(adminClient, topic);

        // then
        assertTopicCreated(adminClient, topic);
    }

    @Test
    public void shouldFailConfiguringTopics() {
        // given
        var topic = new KafkaTopic(TOPIC_NAME, TOPIC_PARTITION);

        var failure = new RuntimeException("Controlled Exception");

        var adminClient = createStubAdminClient();
        when(adminClient.createTopics(anyCollection())).thenThrow(failure);

        // when
        // then
        assertThatThrownBy(() -> topicConfigurer.configure(adminClient, topic))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Could not create Kafka topic")
                .hasCause(failure);
    }

    private void assertTopicCreated(AdminClient adminClient, KafkaTopic topic) {
        var expectedTopic = new NewTopic(topic.name(), topic.partitions(), TOPIC_REPLICATION);
        var expectedTopics = List.of(expectedTopic);

        verify(adminClient).createTopics(expectedTopics);
    }

    private AdminClient createStubAdminClient() {
        return mock(AdminClient.class, RETURNS_DEEP_STUBS);
    }

}
