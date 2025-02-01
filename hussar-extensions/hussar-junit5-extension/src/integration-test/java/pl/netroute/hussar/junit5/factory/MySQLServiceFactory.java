package pl.netroute.hussar.junit5.factory;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.netroute.hussar.service.sql.MySQLDockerServiceConfigurer;
import pl.netroute.hussar.service.sql.api.SQLDatabaseSchema;

import static pl.netroute.hussar.junit5.config.ApplicationEnvironmentVariables.MYSQL_PASSWORD_ENV_VARIABLE;
import static pl.netroute.hussar.junit5.config.ApplicationEnvironmentVariables.MYSQL_URL_ENV_VARIABLE;
import static pl.netroute.hussar.junit5.config.ApplicationEnvironmentVariables.MYSQL_USERNAME_ENV_VARIABLE;
import static pl.netroute.hussar.junit5.config.ApplicationProperties.MYSQL_ALTERNATIVE_PASSWORD_PROPERTY;
import static pl.netroute.hussar.junit5.config.ApplicationProperties.MYSQL_ALTERNATIVE_URL_PROPERTY;
import static pl.netroute.hussar.junit5.config.ApplicationProperties.MYSQL_ALTERNATIVE_USERNAME_PROPERTY;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MySQLServiceFactory {
    public static final String MYSQL_NAME = "mysql-instance";

    public static final SQLDatabaseSchema SCHEMA = SQLDatabaseSchema.scriptLess("hussar");

    public static MySQLDockerServiceConfigurer create() {
        var dockerImageVersion = "8.4.0";

        return MySQLDockerServiceConfigurer
                .newInstance()
                .name(MYSQL_NAME)
                .dockerImageVersion(dockerImageVersion)
                .databaseSchema(SCHEMA)
                .registerEndpointUnderProperty(MYSQL_ALTERNATIVE_URL_PROPERTY)
                .registerEndpointUnderEnvironmentVariable(MYSQL_URL_ENV_VARIABLE)
                .registerUsernameUnderProperty(MYSQL_ALTERNATIVE_USERNAME_PROPERTY)
                .registerUsernameUnderEnvironmentVariable(MYSQL_USERNAME_ENV_VARIABLE)
                .registerPasswordUnderProperty(MYSQL_ALTERNATIVE_PASSWORD_PROPERTY)
                .registerPasswordUnderEnvironmentVariable(MYSQL_PASSWORD_ENV_VARIABLE)
                .done();
    }

}
