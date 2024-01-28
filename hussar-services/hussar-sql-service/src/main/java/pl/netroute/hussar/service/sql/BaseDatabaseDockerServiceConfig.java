package pl.netroute.hussar.service.sql;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import pl.netroute.hussar.core.service.BaseDockerServiceConfig;
import pl.netroute.hussar.service.sql.api.DatabaseSchema;

import java.util.List;

@SuperBuilder
@Getter(AccessLevel.PACKAGE)
abstract class BaseDatabaseDockerServiceConfig extends BaseDockerServiceConfig {
    private final String registerUsernameUnderProperty;
    private final String registerUsernameUnderEnvironmentVariable;

    private final String registerPasswordUnderProperty;
    private final String registerPasswordUnderEnvironmentVariable;

    @NonNull
    private final List<DatabaseSchema> databaseSchemas;
}
