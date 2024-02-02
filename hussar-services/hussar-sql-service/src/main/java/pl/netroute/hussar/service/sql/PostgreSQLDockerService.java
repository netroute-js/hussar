package pl.netroute.hussar.service.sql;

import lombok.NonNull;
import org.testcontainers.containers.GenericContainer;

public class PostgreSQLDockerService extends BaseDatabaseDockerService<SQLDatabaseDockerServiceConfig> {
    private static final int LISTENING_PORT = 5432;

    private static final String POSTGRE_SQL_PASSWORD_ENV = "POSTGRES_PASSWORD";

    private static final String POSTGRE_SQL_USERNAME = "postgres";
    private static final String POSTGRE_SQL_PASSWORD = "test";

    PostgreSQLDockerService(@NonNull SQLDatabaseDockerServiceConfig config) {
        super(config, defaultCredentials());
    }

    @Override
    protected void configureContainer(GenericContainer<?> container) {
        super.configureContainer(container);

        container
                .withExposedPorts(LISTENING_PORT)
                .withEnv(POSTGRE_SQL_PASSWORD_ENV, POSTGRE_SQL_PASSWORD);
    }

    private static DatabaseCredentials defaultCredentials() {
        return new DatabaseCredentials(POSTGRE_SQL_USERNAME, POSTGRE_SQL_PASSWORD);
    }

}
