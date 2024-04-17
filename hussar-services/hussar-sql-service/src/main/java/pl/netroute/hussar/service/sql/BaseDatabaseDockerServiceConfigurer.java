package pl.netroute.hussar.service.sql;

import lombok.NonNull;
import lombok.Singular;
import lombok.experimental.SuperBuilder;
import pl.netroute.hussar.core.service.BaseDockerServiceConfigurer;
import pl.netroute.hussar.service.sql.api.SQLDatabaseSchema;

import java.util.Set;

@SuperBuilder(builderMethodName = "newInstance", buildMethodName = "done")
abstract class BaseDatabaseDockerServiceConfigurer<S extends SQLDatabaseDockerService> extends BaseDockerServiceConfigurer<S> {

    @NonNull
    @Singular
    protected final Set<String> registerUsernameUnderProperties;

    @NonNull
    @Singular
    protected final Set<String> registerUsernameUnderEnvironmentVariables;

    @NonNull
    @Singular
    protected final Set<String> registerPasswordUnderProperties;

    @NonNull
    @Singular
    protected final Set<String> registerPasswordUnderEnvironmentVariables;

    @NonNull
    @Singular
    protected final Set<SQLDatabaseSchema> databaseSchemas;
}
