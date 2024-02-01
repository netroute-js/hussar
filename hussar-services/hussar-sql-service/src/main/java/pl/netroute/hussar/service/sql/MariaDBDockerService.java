package pl.netroute.hussar.service.sql;

import lombok.NonNull;
import org.testcontainers.containers.GenericContainer;

public class MariaDBDockerService extends BaseDatabaseDockerService<SQLDatabaseDockerServiceConfig> {
    private static final int LISTENING_PORT = 3306;

    private static final String MARIA_DB_ROOT_PASSWORD_ENV = "MARIADB_ROOT_PASSWORD";

    private static final String MARIA_DB_ROOT_USERNAME = "root";
    private static final String MARIA_DB_ROOT_PASSWORD = "test";

    MariaDBDockerService(@NonNull SQLDatabaseDockerServiceConfig config) {
        super(config, defaultCredentials());
    }

    @Override
    protected void configureContainer(GenericContainer<?> container) {
        super.configureContainer(container);

        container
                .withExposedPorts(LISTENING_PORT)
                .withEnv(MARIA_DB_ROOT_PASSWORD_ENV, MARIA_DB_ROOT_PASSWORD);
    }

    private static DatabaseCredentials defaultCredentials() {
        return new DatabaseCredentials(MARIA_DB_ROOT_USERNAME, MARIA_DB_ROOT_PASSWORD);
    }

}
