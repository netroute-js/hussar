package pl.netroute.hussar.junit5.config;

import pl.netroute.hussar.core.environment.api.EnvironmentConfigurer;
import pl.netroute.hussar.core.environment.api.EnvironmentConfigurerProvider;
import pl.netroute.hussar.core.environment.api.LocalEnvironmentConfigurer;
import pl.netroute.hussar.junit5.factory.KafkaServiceFactory;
import pl.netroute.hussar.junit5.factory.MariaDBServiceFactory;
import pl.netroute.hussar.junit5.factory.MongoDBServiceFactory;
import pl.netroute.hussar.junit5.factory.MySQLServiceFactory;
import pl.netroute.hussar.junit5.factory.PostgreSQLServiceFactory;
import pl.netroute.hussar.junit5.factory.RabbitMQServiceFactory;
import pl.netroute.hussar.junit5.factory.RedisServiceFactory;
import pl.netroute.hussar.junit5.factory.WiremockServiceFactory;
import pl.netroute.hussar.spring.boot.api.SpringBootApplication;

import static pl.netroute.hussar.junit5.config.ApplicationEnvironmentVariables.METRICS_URL_ENV_VARIABLE;
import static pl.netroute.hussar.junit5.config.ApplicationProperties.METRICS_URL_PROPERTY_VALUE;
import static pl.netroute.hussar.junit5.config.ApplicationProperties.SERVER_AUTH_PROPERTY;
import static pl.netroute.hussar.junit5.config.ApplicationProperties.SERVER_AUTH_PROPERTY_VALUE;

public class SpringTestEnvironmentConfigurerProvider implements EnvironmentConfigurerProvider {

    @Override
    public EnvironmentConfigurer provide() {
        var application = SpringBootApplication.newApplication(SimpleSpringApplication.class);

        var wiremockService = WiremockServiceFactory.create();
        var redisService = RedisServiceFactory.create();
        var rabbitMQService = RabbitMQServiceFactory.create();
        var kafkaService = KafkaServiceFactory.create();
        var mongoDBService = MongoDBServiceFactory.create();
        var mySQLService = MySQLServiceFactory.create();
        var mariaDBService = MariaDBServiceFactory.create();
        var postgreSQLService = PostgreSQLServiceFactory.create();

        return LocalEnvironmentConfigurer
                .newInstance()
                .withProperty(SERVER_AUTH_PROPERTY, SERVER_AUTH_PROPERTY_VALUE)
                .withEnvironmentVariable(METRICS_URL_ENV_VARIABLE, METRICS_URL_PROPERTY_VALUE)
                .withApplication(application)
                .withService(wiremockService)
                .withService(redisService)
                .withService(rabbitMQService)
                .withService(kafkaService)
                .withService(mongoDBService)
                .withService(mySQLService)
                .withService(mariaDBService)
                .withService(postgreSQLService)
                .done();
    }

}
