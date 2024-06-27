package pl.netroute.hussar.junit5.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ApplicationProperties {
    public static final String SERVER_NAME_PROPERTY = "server.name";
    public static final String SERVER_AUTH_PROPERTY = "server.auth";

    public static final String METRICS_URL_PROPERTY = "metrics.url";

    public static final String WIREMOCK_URL_PROPERTY = "application.wiremock.url";
    public static final String WIREMOCK_ALTERNATIVE_URL_PROPERTY = "application.wiremock.alternative.url";

    public static final String REDIS_URL_PROPERTY = "application.redis.url";
    public static final String REDIS_ALTERNATIVE_URL_PROPERTY = "application.redis.alternative.url";
    public static final String REDIS_USERNAME_PROPERTY = "application.redis.username";
    public static final String REDIS_ALTERNATIVE_USERNAME_PROPERTY = "application.redis.alternative.username";
    public static final String REDIS_PASSWORD_PROPERTY = "application.redis.password";
    public static final String REDIS_ALTERNATIVE_PASSWORD_PROPERTY = "application.redis.alternative.password";

    public static final String RABBITMQ_URL_PROPERTY = "application.rabbitmq.url";
    public static final String RABBITMQ_ALTERNATIVE_URL_PROPERTY = "application.rabbitmq.alternative.url";
    public static final String RABBITMQ_USERNAME_PROPERTY = "application.rabbitmq.username";
    public static final String RABBITMQ_ALTERNATIVE_USERNAME_PROPERTY = "application.rabbitmq.alternative.username";
    public static final String RABBITMQ_PASSWORD_PROPERTY = "application.rabbitmq.password";
    public static final String RABBITMQ_ALTERNATIVE_PASSWORD_PROPERTY = "application.rabbitmq.alternative.password";

    public static final String MONGODB_URL_PROPERTY = "application.mongodb.url";
    public static final String MONGODB_ALTERNATIVE_URL_PROPERTY = "application.mongodb.alternative.url";
    public static final String MONGODB_USERNAME_PROPERTY = "application.mongodb.username";
    public static final String MONGODB_ALTERNATIVE_USERNAME_PROPERTY = "application.mongodb.alternative.username";
    public static final String MONGODB_PASSWORD_PROPERTY = "application.mongodb.password";
    public static final String MONGODB_ALTERNATIVE_PASSWORD_PROPERTY = "application.mongodb.alternative.password";

    public static final String MYSQL_URL_PROPERTY = "application.mysql.url";
    public static final String MYSQL_ALTERNATIVE_URL_PROPERTY = "application.mysql.alternative.url";
    public static final String MYSQL_USERNAME_PROPERTY = "application.mysql.username";
    public static final String MYSQL_ALTERNATIVE_USERNAME_PROPERTY = "application.mysql.alternative.username";
    public static final String MYSQL_PASSWORD_PROPERTY = "application.mysql.password";
    public static final String MYSQL_ALTERNATIVE_PASSWORD_PROPERTY = "application.mysql.alternative.password";

    public static final String MARIADB_URL_PROPERTY = "application.mariadb.url";
    public static final String MARIADB_ALTERNATIVE_URL_PROPERTY = "application.mariadb.alternative.url";
    public static final String MARIADB_USERNAME_PROPERTY = "application.mariadb.username";
    public static final String MARIADB_ALTERNATIVE_USERNAME_PROPERTY = "application.mariadb.alternative.username";
    public static final String MARIADB_PASSWORD_PROPERTY = "application.mariadb.password";
    public static final String MARIADB_ALTERNATIVE_PASSWORD_PROPERTY = "application.mariadb.alternative.password";

    public static final String POSTGRESQL_URL_PROPERTY = "application.postgresql.url";
    public static final String POSTGRESQL_ALTERNATIVE_URL_PROPERTY = "application.postgresql.alternative.url";
    public static final String POSTGRESQL_USERNAME_PROPERTY = "application.postgresql.username";
    public static final String POSTGRESQL_ALTERNATIVE_USERNAME_PROPERTY = "application.postgresql.alternative.username";
    public static final String POSTGRESQL_PASSWORD_PROPERTY = "application.postgresql.password";
    public static final String POSTGRESQL_ALTERNATIVE_PASSWORD_PROPERTY = "application.postgresql.alternative.password";

    public static final String KAFKA_URL_PROPERTY = "application.kafka.url";
    public static final String KAFKA_ALTERNATIVE_URL_PROPERTY = "application.kafka.alternative.url";

    public static final String SERVER_NAME_PROPERTY_VALUE = "husar-junit5";
    public static final String SERVER_AUTH_PROPERTY_VALUE = "credentials";
    public static final String METRICS_URL_PROPERTY_VALUE = "https://husar.dev/metrics";
}
