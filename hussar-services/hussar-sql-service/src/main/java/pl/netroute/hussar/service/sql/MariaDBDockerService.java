package pl.netroute.hussar.service.sql;

import lombok.NonNull;
import org.testcontainers.containers.GenericContainer;
import pl.netroute.hussar.core.api.ConfigurationRegistry;
import pl.netroute.hussar.core.service.registerer.EndpointRegisterer;
import pl.netroute.hussar.service.sql.api.SQLDatabaseCredentials;
import pl.netroute.hussar.service.sql.schema.DatabaseSchemaInitializer;

public class MariaDBDockerService extends BaseDatabaseDockerService<SQLDatabaseDockerServiceConfig> {
    private static final int LISTENING_PORT = 3306;

    private static final String MARIA_DB_ROOT_PASSWORD_ENV = "MARIADB_ROOT_PASSWORD";

    private static final String MARIA_DB_ROOT_USERNAME = "root";
    private static final String MARIA_DB_ROOT_PASSWORD = "test";

    MariaDBDockerService(@NonNull GenericContainer<?> container,
                         @NonNull SQLDatabaseDockerServiceConfig config,
                         @NonNull ConfigurationRegistry configurationRegistry,
                         @NonNull EndpointRegisterer endpointRegisterer,
                         @NonNull DatabaseCredentialsRegisterer credentialsRegisterer,
                         @NonNull DatabaseSchemaInitializer schemaInitializer) {
        super(container, config, configurationRegistry, endpointRegisterer, credentialsRegisterer, defaultCredentials(), schemaInitializer);
    }

    @Override
    protected void configureContainer(GenericContainer<?> container) {
        super.configureContainer(container);

        container.withExposedPorts(LISTENING_PORT);
        container.withEnv(MARIA_DB_ROOT_PASSWORD_ENV, MARIA_DB_ROOT_PASSWORD);
    }

    private static SQLDatabaseCredentials defaultCredentials() {
        return new SQLDatabaseCredentials(MARIA_DB_ROOT_USERNAME, MARIA_DB_ROOT_PASSWORD);
    }

}
