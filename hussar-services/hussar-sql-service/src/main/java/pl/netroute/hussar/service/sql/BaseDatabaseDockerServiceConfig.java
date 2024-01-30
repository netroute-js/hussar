package pl.netroute.hussar.service.sql;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import pl.netroute.hussar.core.service.BaseDockerServiceConfig;
import pl.netroute.hussar.service.sql.api.DatabaseSchema;

import java.util.List;

@Getter
@SuperBuilder
public abstract class BaseDatabaseDockerServiceConfig extends BaseDockerServiceConfig {
    private final String registerUsernameUnderProperty;
    private final String registerUsernameUnderEnvironmentVariable;

    private final String registerPasswordUnderProperty;
    private final String registerPasswordUnderEnvironmentVariable;

    @NonNull
    private final List<DatabaseSchema> databaseSchemas;
}
