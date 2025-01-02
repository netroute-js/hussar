package pl.netroute.hussar.service.sql;

import pl.netroute.hussar.core.service.api.Service;
import pl.netroute.hussar.service.sql.api.SQLDatabaseCredentials;

/**
 * Hussar interface responsible for exposing methods of {@link SQLDatabaseDockerService}.
 */
public interface SQLDatabaseDockerService extends Service {

    /**
     * Returns {@link SQLDatabaseCredentials}.
     *
     * @return the actual {@link SQLDatabaseCredentials}.
     */
    SQLDatabaseCredentials getCredentials();

}
