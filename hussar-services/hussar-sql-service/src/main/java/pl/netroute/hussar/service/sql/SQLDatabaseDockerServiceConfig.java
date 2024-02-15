package pl.netroute.hussar.service.sql;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.SuperBuilder;
import pl.netroute.hussar.core.service.BaseDockerServiceConfig;
import pl.netroute.hussar.service.sql.api.SQLDatabaseSchema;

import java.util.List;

@Value
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
class SQLDatabaseDockerServiceConfig extends BaseDockerServiceConfig {
    String registerUsernameUnderProperty;
    String registerUsernameUnderEnvironmentVariable;

    String registerPasswordUnderProperty;
    String registerPasswordUnderEnvironmentVariable;

    @NonNull
    List<SQLDatabaseSchema> databaseSchemas;
}
