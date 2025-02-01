package pl.netroute.hussar.service.sql.api;

import lombok.NonNull;
import org.testcontainers.containers.GenericContainer;
import pl.netroute.hussar.core.configuration.api.ConfigurationRegistry;
import pl.netroute.hussar.core.service.api.Service;
import pl.netroute.hussar.core.service.registerer.EndpointRegisterer;
import pl.netroute.hussar.service.sql.schema.DatabaseSchemaInitializer;

/**
 * Hussar Docker {@link Service} representing PostgreSQL.
 */
public class PostgreSQLDockerService extends BaseDatabaseDockerService<SQLDatabaseDockerServiceConfig> {
    private static final int LISTENING_PORT = 5432;

    private static final String POSTGRE_SQL_PASSWORD_ENV = "POSTGRES_PASSWORD";

    private static final String POSTGRE_SQL_USERNAME = "postgres";
    private static final String POSTGRE_SQL_PASSWORD = "test";

    /**
     * Creates new {@link PostgreSQLDockerService}.
     *
     * @param container - the {@link GenericContainer} used by this {@link PostgreSQLDockerService}.
     * @param config - the {@link SQLDatabaseDockerServiceConfig} used by this {@link PostgreSQLDockerService}.
     * @param configurationRegistry - the {@link ConfigurationRegistry} used by this {@link PostgreSQLDockerService}.
     * @param endpointRegisterer - the  {@link EndpointRegisterer} used by this {@link PostgreSQLDockerService}.
     * @param credentialsRegisterer - the {@link DatabaseCredentialsRegisterer} used by this {@link PostgreSQLDockerService}.
     * @param schemaInitializer - the {@link DatabaseSchemaInitializer} used by this {@link PostgreSQLDockerService}.
     */
    PostgreSQLDockerService(@NonNull GenericContainer<?> container,
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
        container.withEnv(POSTGRE_SQL_PASSWORD_ENV, POSTGRE_SQL_PASSWORD);
    }

    private static SQLDatabaseCredentials defaultCredentials() {
        return new SQLDatabaseCredentials(POSTGRE_SQL_USERNAME, POSTGRE_SQL_PASSWORD);
    }

}
