package pl.netroute.hussar.junit5.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ApplicationEnvironmentVariables {
    public static final String METRICS_URL_ENV_VARIABLE = "METRICS_URL";

    public static final String WIREMOCK_URL_ENV_VARIABLE = "WIREMOCK_URL";

    public static final String REDIS_URL_ENV_VARIABLE = "REDIS_URL";
    public static final String REDIS_USERNAME_ENV_VARIABLE = "REDIS_USERNAME";
    public static final String REDIS_PASSWORD_ENV_VARIABLE = "REDIS_PASSWORD";

    public static final String REDIS_CLUSTER_URL_ENV_VARIABLE = "REDIS_CLUSTER_URL";
    public static final String REDIS_CLUSTER_USERNAME_ENV_VARIABLE = "REDIS_CLUSTER_USERNAME";
    public static final String REDIS_CLUSTER_PASSWORD_ENV_VARIABLE = "REDIS_CLUSTER_PASSWORD";

    public static final String RABBITMQ_URL_ENV_VARIABLE = "RABBITMQ_URL";
    public static final String RABBITMQ_USERNAME_ENV_VARIABLE = "RABBITMQ_USERNAME";
    public static final String RABBITMQ_PASSWORD_ENV_VARIABLE = "RABBITMQ_PASSWORD";

    public static final String MONGODB_URL_ENV_VARIABLE = "MONGODB_URL";
    public static final String MONGODB_USERNAME_ENV_VARIABLE = "MONGODB_USERNAME";
    public static final String MONGODB_PASSWORD_ENV_VARIABLE = "MONGODB_PASSWORD";

    public static final String MYSQL_URL_ENV_VARIABLE = "MYSQL_URL";
    public static final String MYSQL_USERNAME_ENV_VARIABLE = "MYSQL_USERNAME";
    public static final String MYSQL_PASSWORD_ENV_VARIABLE = "MYSQL_PASSWORD";

    public static final String MARIADB_URL_ENV_VARIABLE = "MARIADB_URL";
    public static final String MARIADB_USERNAME_ENV_VARIABLE = "MARIADB_USERNAME";
    public static final String MARIADB_PASSWORD_ENV_VARIABLE = "MARIADB_PASSWORD";

    public static final String POSTGRESQL_URL_ENV_VARIABLE = "POSTGRESQL_URL";
    public static final String POSTGRESQL_USERNAME_ENV_VARIABLE = "POSTGRESQL_USERNAME";
    public static final String POSTGRESQL_PASSWORD_ENV_VARIABLE = "POSTGRESQL_PASSWORD";

    public static final String KAFKA_URL_ENV_VARIABLE = "KAFKA_URL";
}
