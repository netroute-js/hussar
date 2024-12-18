package pl.netroute.hussar.junit5;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import pl.netroute.hussar.core.api.application.Application;
import pl.netroute.hussar.core.api.application.HussarApplication;
import pl.netroute.hussar.core.api.environment.HussarEnvironment;
import pl.netroute.hussar.core.api.service.HussarService;
import pl.netroute.hussar.junit5.config.ClusterSpringTestEnvironmentConfigurerProvider;
import pl.netroute.hussar.service.kafka.KafkaDockerService;
import pl.netroute.hussar.service.nosql.mongodb.MongoDBDockerService;
import pl.netroute.hussar.service.nosql.redis.RedisDockerService;
import pl.netroute.hussar.service.rabbitmq.RabbitMQDockerService;
import pl.netroute.hussar.service.sql.MariaDBDockerService;
import pl.netroute.hussar.service.sql.MySQLDockerService;
import pl.netroute.hussar.service.sql.PostgreSQLDockerService;
import pl.netroute.hussar.service.wiremock.WiremockDockerService;

import static pl.netroute.hussar.junit5.assertion.ApplicationAssertionHelper.assertApplicationBootstrapped;
import static pl.netroute.hussar.junit5.assertion.KafkaAssertionHelper.assertKafkaBootstrapped;
import static pl.netroute.hussar.junit5.assertion.MariaDBAssertionHelper.assertMariaDBBootstrapped;
import static pl.netroute.hussar.junit5.assertion.MongoDBAssertionHelper.assertMongoDBBootstrapped;
import static pl.netroute.hussar.junit5.assertion.MySQLAssertionHelper.assertMySQLBootstrapped;
import static pl.netroute.hussar.junit5.assertion.PostgreSQLAssertionHelper.assertPostgreSQLBootstrapped;
import static pl.netroute.hussar.junit5.assertion.RabbitMQAssertionHelper.assertRabbitMQBootstrapped;
import static pl.netroute.hussar.junit5.assertion.RedisAssertionHelper.assertRedisBootstrapped;
import static pl.netroute.hussar.junit5.assertion.WiremockAssertionHelper.assertWiremockBootstrapped;
import static pl.netroute.hussar.junit5.factory.KafkaServiceFactory.KAFKA_NAME;
import static pl.netroute.hussar.junit5.factory.MariaDBServiceFactory.MARIA_DB_NAME;
import static pl.netroute.hussar.junit5.factory.MongoDBServiceFactory.MONGODB_NAME;
import static pl.netroute.hussar.junit5.factory.MySQLServiceFactory.MYSQL_NAME;
import static pl.netroute.hussar.junit5.factory.PostgreSQLServiceFactory.POSTGRESQL_NAME;
import static pl.netroute.hussar.junit5.factory.RabbitMQServiceFactory.RABBITMQ_NAME;
import static pl.netroute.hussar.junit5.factory.RedisServiceFactory.REDIS_NAME;
import static pl.netroute.hussar.junit5.factory.WiremockServiceFactory.WIREMOCK_NAME;

@ExtendWith(HussarJUnit5Extension.class)
@HussarEnvironment(configurerProvider = ClusterSpringTestEnvironmentConfigurerProvider.class)
public class HussarClusterSpringJUnit5IT {

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
        // when
        // then
        assertApplicationBootstrapped(application);
        assertWiremockBootstrapped(wiremockService, application);
        assertRedisBootstrapped(redisService, application);
        assertRabbitMQBootstrapped(rabbitMQService, application);
        assertKafkaBootstrapped(kafkaService, application);
        assertMongoDBBootstrapped(mongoDBService, application);
        assertMySQLBootstrapped(mySQLService, application);
        assertMariaDBBootstrapped(mariaDBService, application);
        assertPostgreSQLBootstrapped(postgreSQLService, application);
    }

}