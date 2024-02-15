package pl.netroute.hussar.service.sql;

import lombok.Getter;
import lombok.NonNull;
import lombok.Singular;
import lombok.experimental.SuperBuilder;
import pl.netroute.hussar.core.service.BaseDockerServiceConfigurer;
import pl.netroute.hussar.service.sql.api.SQLDatabaseSchema;

import java.util.List;

@Getter
@SuperBuilder(builderMethodName = "newInstance", buildMethodName = "done")
abstract class BaseDatabaseDockerServiceConfigurer<S extends SQLDatabaseDockerService> extends BaseDockerServiceConfigurer<S> {
    private final String registerUsernameUnderProperty;
    private final String registerUsernameUnderEnvironmentVariable;

    private final String registerPasswordUnderProperty;
    private final String registerPasswordUnderEnvironmentVariable;

    @NonNull
    @Singular
    private final List<SQLDatabaseSchema> databaseSchemas;
}
