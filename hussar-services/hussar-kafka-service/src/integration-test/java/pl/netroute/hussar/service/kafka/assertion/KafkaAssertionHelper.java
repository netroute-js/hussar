package pl.netroute.hussar.service.kafka.assertion;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.TopicListing;
import pl.netroute.hussar.core.api.Endpoint;
import pl.netroute.hussar.core.configuration.api.ConfigurationEntry;
import pl.netroute.hussar.core.configuration.api.EnvVariableConfigurationEntry;
import pl.netroute.hussar.core.configuration.api.PropertyConfigurationEntry;
import pl.netroute.hussar.core.helper.EndpointHelper;
import pl.netroute.hussar.service.kafka.api.KafkaDockerService;
import pl.netroute.hussar.service.kafka.api.KafkaTopic;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor
public class KafkaAssertionHelper {
    private static final int SINGLE = 1;

    private static final Duration KAFKA_TIMEOUT = Duration.ofSeconds(5L);

    private final KafkaDockerService kafka;

    public void assertSingleEndpoint() {
        assertThat(kafka.getEndpoints()).hasSize(SINGLE);
    }

    public void asserKafkaAccessible() {
        var endpoint = EndpointHelper.getAnyEndpointOrFail(kafka);

        try(var client = createClient(endpoint)) {
            var nodes = client
                    .describeCluster()
                    .nodes()
                    .get(KAFKA_TIMEOUT.toMillis(), TimeUnit.MILLISECONDS);

            assertThat(nodes).isNotEmpty();
        } catch(Exception ex) {
            throw new IllegalStateException("Could not check Kafka accessibility", ex);
        }
    }

    public void assertKafkaNotAccessible() {
        var endpoint = EndpointHelper.getAnyEndpointOrFail(kafka);

        assertKafkaNotAccessible(endpoint);
    }

    public void assertKafkaNotAccessible(@NonNull Endpoint endpoint) {
        try(var client = createClient(endpoint)) {
            client
                    .describeCluster()
                    .nodes()
                    .get(KAFKA_TIMEOUT.toMillis(), TimeUnit.MILLISECONDS);

            throw new AssertionError("Expected Kafka to not be accessible");
        } catch(Exception ex) {
        }
    }

    public void assertTopicsCreated(@NonNull List<KafkaTopic> topics) {
        var endpoint = EndpointHelper.getAnyEndpointOrFail(kafka);

        var actualTopicNames = listTopics(endpoint);

        var expectedTopicNames = topics
                .stream()
                .map(KafkaTopic::name)
                .toList();

        assertThat(actualTopicNames).containsExactlyInAnyOrderElementsOf(expectedTopicNames);
    }

    public void assertNoTopicsCreated() {
        var endpoint = EndpointHelper.getAnyEndpointOrFail(kafka);

        var topics = listTopics(endpoint);
        assertThat(topics).isEmpty();
    }

    public void assertRegisteredEndpointUnderProperty(@NonNull String registeredProperty) {
        var endpoint = EndpointHelper.getAnyEndpointOrFail(kafka);

        assertRegisteredEntryInConfigRegistry(registeredProperty, endpoint.address(), PropertyConfigurationEntry.class);
    }

    public void assertRegisteredEndpointUnderEnvironmentVariable(@NonNull String registeredEnvVariable) {
        var endpoint = EndpointHelper.getAnyEndpointOrFail(kafka);

        assertRegisteredEntryInConfigRegistry(registeredEnvVariable, endpoint.address(), EnvVariableConfigurationEntry.class);
    }

    public void assertNoEntriesRegistered() {
        var entriesRegistered = kafka
                .getConfigurationRegistry()
                .getEntries();

        assertThat(entriesRegistered).isEmpty();
    }

    private AdminClient createClient(Endpoint endpoint) {
        var connectionProperties = Map.<String, Object>of(
                CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, endpoint.address(),
                CommonClientConfigs.REQUEST_TIMEOUT_MS_CONFIG, KAFKA_TIMEOUT.toMillis() + "",
                CommonClientConfigs.DEFAULT_API_TIMEOUT_MS_CONFIG, KAFKA_TIMEOUT.toMillis() + ""
        );

        return AdminClient.create(connectionProperties);
    }

    private void assertRegisteredEntryInConfigRegistry(String entryName, String entryValue, Class<? extends ConfigurationEntry> configType) {
        var configRegistry = kafka.getConfigurationRegistry();

        configRegistry
                .getEntries()
                .stream()
                .filter(configEntry -> configEntry.getClass().equals(configType))
                .filter(configEntry -> configEntry.name().equals(entryName))
                .findFirst()
                .ifPresentOrElse(
                        configEntry -> assertThat(configEntry.value()).isEqualTo(entryValue),
                        () -> { throw new AssertionError("Expected registered entry in config registry. Found none"); }
                );
    }

    private List<String> listTopics(Endpoint endpoint) {
        try(var client = createClient(endpoint)) {
            return client
                    .listTopics()
                    .listings()
                    .get(KAFKA_TIMEOUT.toMillis(), TimeUnit.MILLISECONDS)
                    .stream()
                    .filter(topic -> !topic.isInternal())
                    .map(TopicListing::name)
                    .toList();
        } catch(Exception ex) {
            throw new IllegalStateException("Could not check Kafka accessibility", ex);
        }
    }

}
