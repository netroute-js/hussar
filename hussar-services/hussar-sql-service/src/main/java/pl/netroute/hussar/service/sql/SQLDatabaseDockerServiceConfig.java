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

    @NonNull
    Set<String> registerUsernameUnderProperties;

    @NonNull
    Set<String> registerUsernameUnderEnvironmentVariables;

    @NonNull
    Set<String> registerPasswordUnderProperties;

    @NonNull
    Set<String> registerPasswordUnderEnvironmentVariables;

    @NonNull
    Set<SQLDatabaseSchema> databaseSchemas;
}
