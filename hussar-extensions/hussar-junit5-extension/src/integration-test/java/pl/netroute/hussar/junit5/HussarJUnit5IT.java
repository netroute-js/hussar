package pl.netroute.hussar.junit5;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import pl.netroute.hussar.core.annotation.HussarApplication;
import pl.netroute.hussar.core.annotation.HussarEnvironment;
import pl.netroute.hussar.core.annotation.HussarService;
import pl.netroute.hussar.core.api.Application;
import pl.netroute.hussar.junit5.client.ClientFactory;
import pl.netroute.hussar.junit5.client.SimpleApplicationClient;
import pl.netroute.hussar.junit5.config.TestEnvironmentConfigurerProvider;
import pl.netroute.hussar.service.kafka.KafkaDockerService;
import pl.netroute.hussar.service.nosql.mongodb.MongoDBDockerService;
import pl.netroute.hussar.service.nosql.redis.RedisDockerService;
import pl.netroute.hussar.service.rabbitmq.RabbitMQDockerService;
import pl.netroute.hussar.service.sql.MariaDBDockerService;
import pl.netroute.hussar.service.sql.MySQLDockerService;
import pl.netroute.hussar.service.sql.PostgreSQLDockerService;
import pl.netroute.hussar.service.wiremock.WiremockDockerService;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.netroute.hussar.junit5.assertion.ApplicationPropertiesAssertionHelper.assertPropertyConfigured;
import static pl.netroute.hussar.junit5.assertion.KafkaAssertionHelper.assertKafkaBootstrapped;
import static pl.netroute.hussar.junit5.assertion.MariaDBAssertionHelper.assertMariaDBBootstrapped;
import static pl.netroute.hussar.junit5.assertion.MongoDBAssertionHelper.assertMongoDBBootstrapped;
import static pl.netroute.hussar.junit5.assertion.MySQLAssertionHelper.assertMySQLBootstrapped;
import static pl.netroute.hussar.junit5.assertion.PostgreSQLAssertionHelper.assertPostgreSQLBootstrapped;
import static pl.netroute.hussar.junit5.assertion.RabbitMQAssertionHelper.assertRabbitMQBootstrapped;
import static pl.netroute.hussar.junit5.assertion.RedisAssertionHelper.assertRedisBootstrapped;
import static pl.netroute.hussar.junit5.assertion.WiremockAssertionHelper.assertWiremockBootstrapped;
import static pl.netroute.hussar.junit5.config.ApplicationProperties.METRICS_URL_PROPERTY;
import static pl.netroute.hussar.junit5.config.ApplicationProperties.METRICS_URL_PROPERTY_VALUE;
import static pl.netroute.hussar.junit5.config.ApplicationProperties.SERVER_AUTH_PROPERTY;
import static pl.netroute.hussar.junit5.config.ApplicationProperties.SERVER_AUTH_PROPERTY_VALUE;
import static pl.netroute.hussar.junit5.config.ApplicationProperties.SERVER_NAME_PROPERTY;
import static pl.netroute.hussar.junit5.config.ApplicationProperties.SERVER_NAME_PROPERTY_VALUE;
import static pl.netroute.hussar.junit5.factory.KafkaServiceFactory.KAFKA_NAME;
import static pl.netroute.hussar.junit5.factory.MariaDBServiceFactory.MARIA_DB_NAME;
import static pl.netroute.hussar.junit5.factory.MongoDBServiceFactory.MONGODB_NAME;
import static pl.netroute.hussar.junit5.factory.MySQLServiceFactory.MYSQL_NAME;
import static pl.netroute.hussar.junit5.factory.PostgreSQLServiceFactory.POSTGRESQL_NAME;
import static pl.netroute.hussar.junit5.factory.RabbitMQServiceFactory.RABBITMQ_NAME;
import static pl.netroute.hussar.junit5.factory.RedisServiceFactory.REDIS_NAME;
import static pl.netroute.hussar.junit5.factory.WiremockServiceFactory.WIREMOCK_NAME;

@ExtendWith(HussarJUnit5Extension.class)
@HussarEnvironment(configurerProvider = TestEnvironmentConfigurerProvider.class)
public class HussarJUnit5IT {
    private static final String PING_RESPONSE = "pong";

    @HussarApplication
    Application application;

    @HussarService(name = WIREMOCK_NAME)
    WiremockDockerService wiremockService;

    @HussarService(name = REDIS_NAME)
    RedisDockerService redisService;

    @HussarService(name = RABBITMQ_NAME)
    RabbitMQDockerService rabbitMQService;

    @HussarService(name = KAFKA_NAME)
    KafkaDockerService kafkaService;

    @HussarService(name = MONGODB_NAME)
    MongoDBDockerService mongoDBService;

    @HussarService(name = MYSQL_NAME)
    MySQLDockerService mySQLService;

    @HussarService(name = MARIA_DB_NAME)
    MariaDBDockerService mariaDBService;

    @HussarService(name = POSTGRESQL_NAME)
    PostgreSQLDockerService postgreSQLService;

    @Test
    public void shouldStartupEnvironment() {
        // given
        var applicationClient = applicationClient(application);

        // when
        // then
        assertPingEndpointAccessible(applicationClient);
        assertPropertyConfigured(SERVER_NAME_PROPERTY, SERVER_NAME_PROPERTY_VALUE, applicationClient);
        assertPropertyConfigured(SERVER_AUTH_PROPERTY, SERVER_AUTH_PROPERTY_VALUE, applicationClient);
        assertPropertyConfigured(METRICS_URL_PROPERTY, METRICS_URL_PROPERTY_VALUE, applicationClient);

        assertWiremockBootstrapped(wiremockService, applicationClient);
        assertRedisBootstrapped(redisService, applicationClient);
        assertRabbitMQBootstrapped(rabbitMQService, applicationClient);
        assertKafkaBootstrapped(kafkaService, applicationClient);
        assertMongoDBBootstrapped(mongoDBService, applicationClient);
        assertMySQLBootstrapped(mySQLService, applicationClient);
        assertMariaDBBootstrapped(mariaDBService, applicationClient);
        assertPostgreSQLBootstrapped(postgreSQLService, applicationClient);
    }

    private void assertPingEndpointAccessible(SimpleApplicationClient client) {
        var pingResponse = client.ping();

        assertThat(pingResponse).isEqualTo(PING_RESPONSE);
    }

    private SimpleApplicationClient applicationClient(Application application) {
        var endpoint = application
                .getEndpoints()
                .get(0);

        return ClientFactory.create(endpoint, SimpleApplicationClient.class);
    }
}
