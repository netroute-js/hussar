package pl.netroute.hussar.service.sql;

import pl.netroute.hussar.core.api.Service;

public interface DatabaseDockerService extends Service {
    DatabaseCredentials getCredentials();
}
