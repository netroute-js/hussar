package pl.netroute.hussar.junit5;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import pl.netroute.hussar.core.application.api.Application;
import pl.netroute.hussar.core.application.api.HussarApplication;
import pl.netroute.hussar.core.environment.api.HussarEnvironment;
import pl.netroute.hussar.core.service.api.HussarService;
import pl.netroute.hussar.junit5.api.HussarJUnit5Extension;
import pl.netroute.hussar.junit5.config.SpringTestEnvironmentConfigurerProvider;
import pl.netroute.hussar.service.kafka.api.KafkaDockerService;
import pl.netroute.hussar.service.nosql.mongodb.api.MongoDBDockerService;
import pl.netroute.hussar.service.nosql.redis.api.RedisDockerService;
import pl.netroute.hussar.service.rabbitmq.api.RabbitMQDockerService;
import pl.netroute.hussar.service.sql.api.MariaDBDockerService;
import pl.netroute.hussar.service.sql.api.MySQLDockerService;
import pl.netroute.hussar.service.sql.api.PostgreSQLDockerService;
import pl.netroute.hussar.service.wiremock.api.WiremockDockerService;

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
@HussarEnvironment(configurerProvider = SpringTestEnvironmentConfigurerProvider.class)
public class HussarSpringJUnit5IT {

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
