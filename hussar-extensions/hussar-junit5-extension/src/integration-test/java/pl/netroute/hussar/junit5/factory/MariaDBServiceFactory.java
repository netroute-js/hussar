package pl.netroute.hussar.junit5.factory;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.netroute.hussar.service.sql.api.MariaDBDockerServiceConfigurer;
import pl.netroute.hussar.service.sql.api.SQLDatabaseSchema;

import static pl.netroute.hussar.junit5.config.ApplicationEnvironmentVariables.MARIADB_PASSWORD_ENV_VARIABLE;
import static pl.netroute.hussar.junit5.config.ApplicationEnvironmentVariables.MARIADB_URL_ENV_VARIABLE;
import static pl.netroute.hussar.junit5.config.ApplicationEnvironmentVariables.MARIADB_USERNAME_ENV_VARIABLE;
import static pl.netroute.hussar.junit5.config.ApplicationProperties.MARIADB_ALTERNATIVE_PASSWORD_PROPERTY;
import static pl.netroute.hussar.junit5.config.ApplicationProperties.MARIADB_ALTERNATIVE_URL_PROPERTY;
import static pl.netroute.hussar.junit5.config.ApplicationProperties.MARIADB_ALTERNATIVE_USERNAME_PROPERTY;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MariaDBServiceFactory {
    public static final String MARIA_DB_NAME = "mariadb-instance";

    public static final SQLDatabaseSchema SCHEMA = SQLDatabaseSchema.scriptLess("hussar");

    public static MariaDBDockerServiceConfigurer create() {
        var dockerImageVersion = "11.4.2";

        return MariaDBDockerServiceConfigurer
                .newInstance()
                .name(MARIA_DB_NAME)
                .dockerImageVersion(dockerImageVersion)
                .databaseSchema(SCHEMA)
                .registerEndpointUnderProperty(MARIADB_ALTERNATIVE_URL_PROPERTY)
                .registerEndpointUnderEnvironmentVariable(MARIADB_URL_ENV_VARIABLE)
                .registerUsernameUnderProperty(MARIADB_ALTERNATIVE_USERNAME_PROPERTY)
                .registerUsernameUnderEnvironmentVariable(MARIADB_USERNAME_ENV_VARIABLE)
                .registerPasswordUnderProperty(MARIADB_ALTERNATIVE_PASSWORD_PROPERTY)
                .registerPasswordUnderEnvironmentVariable(MARIADB_PASSWORD_ENV_VARIABLE)
                .done();
    }

}
