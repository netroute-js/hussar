package pl.netroute.hussar.service.sql;

import lombok.NonNull;
import org.testcontainers.containers.GenericContainer;

public class PostgresSQLDockerService extends BaseDatabaseDockerService<SQLDatabaseDockerServiceConfig> {
    private static final int LISTENING_PORT = 5432;

    private static final String POSTGRES_SQL_PASSWORD_ENV = "POSTGRES_PASSWORD";

    private static final String POSTGRES_SQL_USERNAME = "postgres";
    private static final String POSTGRES_SQL_PASSWORD = "test";

    PostgresSQLDockerService(@NonNull SQLDatabaseDockerServiceConfig config) {
        super(config, defaultCredentials());
    }

    @Override
    protected void configureContainer(GenericContainer<?> container) {
        super.configureContainer(container);

        container
                .withExposedPorts(LISTENING_PORT)
                .withEnv(POSTGRES_SQL_PASSWORD_ENV, POSTGRES_SQL_PASSWORD);
    }

    private static DatabaseCredentials defaultCredentials() {
        return new DatabaseCredentials(POSTGRES_SQL_USERNAME, POSTGRES_SQL_PASSWORD);
    }

}
