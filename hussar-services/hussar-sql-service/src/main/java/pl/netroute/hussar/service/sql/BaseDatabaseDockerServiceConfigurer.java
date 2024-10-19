package pl.netroute.hussar.service.sql;

import lombok.NonNull;
import lombok.Singular;
import lombok.experimental.SuperBuilder;
import pl.netroute.hussar.core.service.BaseDockerServiceConfigurer;
import pl.netroute.hussar.service.sql.api.SQLDatabaseSchema;

import java.util.Set;

/**
 * A base configurer class for all Hussar Docker {@link SQLDatabaseDockerService}. The configurer is responsible for configuring {@link SQLDatabaseDockerService}.
 *
 * @param <S> the type of the {@link SQLDatabaseDockerService} to be configured.
 */
@SuperBuilder(builderMethodName = "newInstance", buildMethodName = "done")
abstract class BaseDatabaseDockerServiceConfigurer<S extends SQLDatabaseDockerService> extends BaseDockerServiceConfigurer<S> {

    /**
     * Set of properties to be used to register SQL DB username under.
     */
    @NonNull
    @Singular
    protected final Set<String> registerUsernameUnderProperties;

    /**
     * Set of environment variables to be used to register SQL DB username under.
     */
    @NonNull
    @Singular
    protected final Set<String> registerUsernameUnderEnvironmentVariables;

    /**
     * Set of properties to be used to register SQL DB password under.
     */
    @NonNull
    @Singular
    protected final Set<String> registerPasswordUnderProperties;

    /**
     * Set of environment variables to be used to register SQL DB password under.
     */
    @NonNull
    @Singular
    protected final Set<String> registerPasswordUnderEnvironmentVariables;

    /**
     * Set of SQL DB schemas to be created on startup.
     */
    @NonNull
    @Singular
    protected final Set<SQLDatabaseSchema> databaseSchemas;
}
