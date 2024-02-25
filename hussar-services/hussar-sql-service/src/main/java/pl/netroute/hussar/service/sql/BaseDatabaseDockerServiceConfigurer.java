package pl.netroute.hussar.service.sql;

import lombok.NonNull;
import lombok.Singular;
import lombok.experimental.SuperBuilder;
import pl.netroute.hussar.core.service.BaseDockerServiceConfigurer;
import pl.netroute.hussar.service.sql.api.SQLDatabaseSchema;

import java.util.Set;

@SuperBuilder(builderMethodName = "newInstance", buildMethodName = "done")
abstract class BaseDatabaseDockerServiceConfigurer<S extends SQLDatabaseDockerService> extends BaseDockerServiceConfigurer<S> {

    @Singular
    protected final Set<String> registerUsernameUnderProperties;

    @Singular
    protected final Set<String> registerUsernameUnderEnvironmentVariables;

    @Singular
    protected final Set<String> registerPasswordUnderProperties;

    @Singular
    protected final Set<String> registerPasswordUnderEnvironmentVariables;

    @NonNull
    @Singular
    protected final Set<SQLDatabaseSchema> databaseSchemas;
}
