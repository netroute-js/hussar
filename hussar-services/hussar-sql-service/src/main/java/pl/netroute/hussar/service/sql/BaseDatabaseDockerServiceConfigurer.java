package pl.netroute.hussar.service.sql;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Singular;
import lombok.experimental.SuperBuilder;
import pl.netroute.hussar.core.service.BaseDockerServiceConfigurer;
import pl.netroute.hussar.service.sql.api.DatabaseSchema;

import java.util.List;

@Getter(AccessLevel.PROTECTED)
@SuperBuilder(builderMethodName = "newInstance", buildMethodName = "done")
abstract class BaseDatabaseDockerServiceConfigurer<S extends DatabaseDockerService> extends BaseDockerServiceConfigurer<S> {
    private final String registerUsernameUnderProperty;
    private final String registerUsernameUnderEnvironmentVariable;

    private final String registerPasswordUnderProperty;
    private final String registerPasswordUnderEnvironmentVariable;

    @NonNull
    @Singular
    private final List<DatabaseSchema> databaseSchemas;
}