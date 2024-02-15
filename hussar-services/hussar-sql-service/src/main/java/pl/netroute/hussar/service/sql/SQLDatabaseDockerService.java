package pl.netroute.hussar.service.sql;

import pl.netroute.hussar.core.api.Service;
import pl.netroute.hussar.service.sql.api.SQLDatabaseCredentials;

public interface SQLDatabaseDockerService extends Service {
    SQLDatabaseCredentials getCredentials();
}
