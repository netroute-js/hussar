package pl.netroute.hussar.service.sql;

import lombok.NonNull;
import org.testcontainers.containers.GenericContainer;
import pl.netroute.hussar.service.sql.api.SQLDatabaseCredentials;

public class MySQLDockerService extends BaseDatabaseDockerService<SQLDatabaseDockerServiceConfig> {
    private static final int LISTENING_PORT = 3306;

    private static final String MYSQL_ROOT_PASSWORD_ENV = "MYSQL_ROOT_PASSWORD";

    private static final String MYSQL_ROOT_USERNAME = "root";
    private static final String MYSQL_ROOT_PASSWORD = "test";

    MySQLDockerService(@NonNull SQLDatabaseDockerServiceConfig config) {
        super(config, defaultCredentials());
    }

    @Override
    protected void configureContainer(GenericContainer<?> container) {
        super.configureContainer(container);

        container
                .withExposedPorts(LISTENING_PORT)
                .withEnv(MYSQL_ROOT_PASSWORD_ENV, MYSQL_ROOT_PASSWORD);
    }

    private static SQLDatabaseCredentials defaultCredentials() {
        return new SQLDatabaseCredentials(MYSQL_ROOT_USERNAME, MYSQL_ROOT_PASSWORD);
    }

}
