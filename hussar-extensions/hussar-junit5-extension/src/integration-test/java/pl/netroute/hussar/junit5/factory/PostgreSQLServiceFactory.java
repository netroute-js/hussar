package pl.netroute.hussar.junit5.factory;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.netroute.hussar.service.sql.api.PostgreSQLDockerServiceConfigurer;
import pl.netroute.hussar.service.sql.api.SQLDatabaseSchema;

import static pl.netroute.hussar.junit5.config.ApplicationEnvironmentVariables.POSTGRESQL_PASSWORD_ENV_VARIABLE;
import static pl.netroute.hussar.junit5.config.ApplicationEnvironmentVariables.POSTGRESQL_URL_ENV_VARIABLE;
import static pl.netroute.hussar.junit5.config.ApplicationEnvironmentVariables.POSTGRESQL_USERNAME_ENV_VARIABLE;
import static pl.netroute.hussar.junit5.config.ApplicationProperties.POSTGRESQL_ALTERNATIVE_PASSWORD_PROPERTY;
import static pl.netroute.hussar.junit5.config.ApplicationProperties.POSTGRESQL_ALTERNATIVE_URL_PROPERTY;
import static pl.netroute.hussar.junit5.config.ApplicationProperties.POSTGRESQL_ALTERNATIVE_USERNAME_PROPERTY;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PostgreSQLServiceFactory {
    public static final String POSTGRESQL_NAME = "postgresql-instance";

    public static final SQLDatabaseSchema SCHEMA = SQLDatabaseSchema.scriptLess("hussar");

    public static PostgreSQLDockerServiceConfigurer create() {
        var dockerImageVersion = "16-alpine";

        return PostgreSQLDockerServiceConfigurer
                .newInstance()
                .name(POSTGRESQL_NAME)
                .dockerImageVersion(dockerImageVersion)
                .databaseSchema(SCHEMA)
                .registerEndpointUnderProperty(POSTGRESQL_ALTERNATIVE_URL_PROPERTY)
                .registerEndpointUnderEnvironmentVariable(POSTGRESQL_URL_ENV_VARIABLE)
                .registerUsernameUnderProperty(POSTGRESQL_ALTERNATIVE_USERNAME_PROPERTY)
                .registerUsernameUnderEnvironmentVariable(POSTGRESQL_USERNAME_ENV_VARIABLE)
                .registerPasswordUnderProperty(POSTGRESQL_ALTERNATIVE_PASSWORD_PROPERTY)
                .registerPasswordUnderEnvironmentVariable(POSTGRESQL_PASSWORD_ENV_VARIABLE)
                .done();
    }

}
