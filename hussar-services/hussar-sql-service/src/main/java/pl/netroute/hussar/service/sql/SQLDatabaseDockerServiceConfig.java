package pl.netroute.hussar.service.sql;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.SuperBuilder;
import pl.netroute.hussar.core.service.BaseDockerServiceConfig;
import pl.netroute.hussar.service.sql.api.SQLDatabaseSchema;

import java.util.Set;

@Value
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
class SQLDatabaseDockerServiceConfig extends BaseDockerServiceConfig {
    Set<String> registerUsernameUnderProperties;
    Set<String> registerUsernameUnderEnvironmentVariables;

    Set<String> registerPasswordUnderProperties;
    Set<String> registerPasswordUnderEnvironmentVariables;

    @NonNull
    Set<SQLDatabaseSchema> databaseSchemas;
}
