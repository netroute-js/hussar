package pl.netroute.hussar.service.sql.api;

import lombok.NonNull;
import org.testcontainers.containers.GenericContainer;
import pl.netroute.hussar.core.configuration.api.ConfigurationRegistry;
import pl.netroute.hussar.core.network.api.NetworkConfigurer;
import pl.netroute.hussar.core.service.api.Service;
import pl.netroute.hussar.core.service.registerer.EndpointRegisterer;
import pl.netroute.hussar.service.sql.schema.DatabaseSchemaInitializer;

/**
 * Hussar Docker {@link Service} representing MySQL.
 */
public class MySQLDockerService extends BaseDatabaseDockerService<SQLDatabaseDockerServiceConfig> {
    private static final int LISTENING_PORT = 3306;

    private static final String MYSQL_ROOT_PASSWORD_ENV = "MYSQL_ROOT_PASSWORD";

    private static final String MYSQL_ROOT_USERNAME = "root";
    private static final String MYSQL_ROOT_PASSWORD = "test";

    /**
     * Creates new {@link MySQLDockerService}.
     *
     * @param container - the {@link GenericContainer} used by this {@link MySQLDockerService}.
     * @param config - the {@link SQLDatabaseDockerServiceConfig} used by this {@link MySQLDockerService}.
     * @param configurationRegistry - the {@link ConfigurationRegistry} used by this {@link MySQLDockerService}.
     * @param endpointRegisterer - the  {@link EndpointRegisterer} used by this {@link MySQLDockerService}.
     * @param networkConfigurer - the  {@link NetworkConfigurer} used by this {@link BaseDatabaseDockerService}.
     * @param credentialsRegisterer - the {@link DatabaseCredentialsRegisterer} used by this {@link MySQLDockerService}.
     * @param schemaInitializer - the {@link DatabaseSchemaInitializer} used by this {@link MySQLDockerService}.
     */
    MySQLDockerService(@NonNull GenericContainer<?> container,
                       @NonNull SQLDatabaseDockerServiceConfig config,
                       @NonNull ConfigurationRegistry configurationRegistry,
                       @NonNull EndpointRegisterer endpointRegisterer,
                       @NonNull NetworkConfigurer networkConfigurer,
                       @NonNull DatabaseCredentialsRegisterer credentialsRegisterer,
                       @NonNull DatabaseSchemaInitializer schemaInitializer) {
        super(
                container,
                config,
                configurationRegistry,
                endpointRegisterer,
                networkConfigurer,
                credentialsRegisterer,
                defaultCredentials(),
                schemaInitializer
        );
    }

    @Override
    protected void configureContainer(GenericContainer<?> container) {
        super.configureContainer(container);

        container.withExposedPorts(LISTENING_PORT);
        container.withEnv(MYSQL_ROOT_PASSWORD_ENV, MYSQL_ROOT_PASSWORD);
    }

    private static SQLDatabaseCredentials defaultCredentials() {
        return new SQLDatabaseCredentials(MYSQL_ROOT_USERNAME, MYSQL_ROOT_PASSWORD);
    }

}
