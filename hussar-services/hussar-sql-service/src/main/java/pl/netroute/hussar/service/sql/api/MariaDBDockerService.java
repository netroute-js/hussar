package pl.netroute.hussar.service.sql.api;

import lombok.NonNull;
import org.testcontainers.containers.GenericContainer;
import pl.netroute.hussar.core.configuration.api.ConfigurationRegistry;
import pl.netroute.hussar.core.service.api.Service;
import pl.netroute.hussar.core.service.registerer.EndpointRegisterer;
import pl.netroute.hussar.service.sql.schema.DatabaseSchemaInitializer;

/**
 * Hussar Docker {@link Service} representing MariaDB.
 */
public class MariaDBDockerService extends BaseDatabaseDockerService<SQLDatabaseDockerServiceConfig> {
    private static final int LISTENING_PORT = 3306;

    private static final String MARIA_DB_ROOT_PASSWORD_ENV = "MARIADB_ROOT_PASSWORD";

    private static final String MARIA_DB_ROOT_USERNAME = "root";
    private static final String MARIA_DB_ROOT_PASSWORD = "test";

    /**
     * Creates new {@link MariaDBDockerService}.
     *
     * @param container - the {@link GenericContainer} used by this {@link MariaDBDockerService}.
     * @param config - the {@link SQLDatabaseDockerServiceConfig} used by this {@link MariaDBDockerService}.
     * @param configurationRegistry - the {@link ConfigurationRegistry} used by this {@link MariaDBDockerService}.
     * @param endpointRegisterer - the  {@link EndpointRegisterer} used by this {@link MariaDBDockerService}.
     * @param credentialsRegisterer - the {@link DatabaseCredentialsRegisterer} used by this {@link MariaDBDockerService}.
     * @param schemaInitializer - the {@link DatabaseSchemaInitializer} used by this {@link MariaDBDockerService}.
     */
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
