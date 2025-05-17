package pl.netroute.hussar.junit5;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import pl.netroute.hussar.core.application.api.Application;
import pl.netroute.hussar.core.application.api.HussarApplication;
import pl.netroute.hussar.core.environment.api.HussarEnvironment;
import pl.netroute.hussar.core.network.api.HussarNetworkRestore;
import pl.netroute.hussar.core.network.api.NetworkRestore;
import pl.netroute.hussar.core.service.api.HussarService;
import pl.netroute.hussar.junit5.api.HussarJUnit5Extension;
import pl.netroute.hussar.junit5.config.SpringTestEnvironmentConfigurerProvider;
import pl.netroute.hussar.service.kafka.api.KafkaDockerService;
import pl.netroute.hussar.service.nosql.mongodb.api.MongoDBDockerService;
import pl.netroute.hussar.service.nosql.redis.api.RedisClusterDockerService;
import pl.netroute.hussar.service.nosql.redis.api.RedisDockerService;
import pl.netroute.hussar.service.rabbitmq.api.RabbitMQDockerService;
import pl.netroute.hussar.service.sql.api.MariaDBDockerService;
import pl.netroute.hussar.service.sql.api.MySQLDockerService;
import pl.netroute.hussar.service.sql.api.PostgreSQLDockerService;
import pl.netroute.hussar.service.wiremock.api.WiremockDockerService;

import static pl.netroute.hussar.junit5.assertion.ApplicationAssertionHelper.assertApplicationBootstrapped;
import static pl.netroute.hussar.junit5.assertion.ApplicationAssertionHelper.assertApplicationDependencyInjected;
import static pl.netroute.hussar.junit5.assertion.KafkaAssertionHelper.assertKafkaBootstrapped;
import static pl.netroute.hussar.junit5.assertion.MariaDBAssertionHelper.assertMariaDBBootstrapped;
import static pl.netroute.hussar.junit5.assertion.MongoDBAssertionHelper.assertMongoDBBootstrapped;
import static pl.netroute.hussar.junit5.assertion.MySQLAssertionHelper.assertMySQLBootstrapped;
import static pl.netroute.hussar.junit5.assertion.NetworkRestoreAssertionHelper.assertNetworkRestoreInjected;
import static pl.netroute.hussar.junit5.assertion.PostgreSQLAssertionHelper.assertPostgreSQLBootstrapped;
import static pl.netroute.hussar.junit5.assertion.RabbitMQAssertionHelper.assertRabbitMQBootstrapped;
import static pl.netroute.hussar.junit5.assertion.RedisAssertionHelper.assertRedisBootstrapped;
import static pl.netroute.hussar.junit5.assertion.RedisClusterAssertionHelper.assertRedisClusterBootstrapped;
import static pl.netroute.hussar.junit5.assertion.WiremockAssertionHelper.assertWiremockBootstrapped;
import static pl.netroute.hussar.junit5.factory.KafkaServiceFactory.KAFKA_NAME;
import static pl.netroute.hussar.junit5.factory.MariaDBServiceFactory.MARIA_DB_NAME;
import static pl.netroute.hussar.junit5.factory.MongoDBServiceFactory.MONGODB_NAME;
import static pl.netroute.hussar.junit5.factory.MySQLServiceFactory.MYSQL_NAME;
import static pl.netroute.hussar.junit5.factory.PostgreSQLServiceFactory.POSTGRESQL_NAME;
import static pl.netroute.hussar.junit5.factory.RabbitMQServiceFactory.RABBITMQ_NAME;
import static pl.netroute.hussar.junit5.factory.RedisClusterServiceFactory.REDIS_CLUSTER_NAME;
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

    @HussarService(name = REDIS_CLUSTER_NAME)
    RedisClusterDockerService redisClusterService;

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

    @HussarNetworkRestore
    private NetworkRestore networkRestore;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    public void shouldStartupEnvironment() {
        // given
        // when
        // then
        assertApplicationBootstrapped(application);
        assertApplicationDependencyInjected(objectMapper);
        assertNetworkRestoreInjected(networkRestore);
        assertWiremockBootstrapped(wiremockService, application);
        assertRedisBootstrapped(redisService, application);
        assertRedisClusterBootstrapped(redisClusterService, application);
        assertRabbitMQBootstrapped(rabbitMQService, application);
        assertKafkaBootstrapped(kafkaService, application);
        assertMongoDBBootstrapped(mongoDBService, application);
        assertMySQLBootstrapped(mySQLService, application);
        assertMariaDBBootstrapped(mariaDBService, application);
        assertPostgreSQLBootstrapped(postgreSQLService, application);
    }

}
