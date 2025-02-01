package pl.netroute.hussar.service.sql.api;

import pl.netroute.hussar.core.service.api.Service;

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
