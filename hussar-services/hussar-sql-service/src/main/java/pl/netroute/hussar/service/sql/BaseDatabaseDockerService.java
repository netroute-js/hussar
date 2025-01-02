package pl.netroute.hussar.service.sql;

import lombok.NonNull;
import org.testcontainers.containers.GenericContainer;
import pl.netroute.hussar.core.configuration.api.ConfigurationRegistry;
import pl.netroute.hussar.core.service.api.ServiceStartupContext;
import pl.netroute.hussar.core.helper.EndpointHelper;
import pl.netroute.hussar.core.service.BaseDockerService;
import pl.netroute.hussar.core.service.registerer.EndpointRegisterer;
import pl.netroute.hussar.service.sql.api.SQLDatabaseCredentials;
import pl.netroute.hussar.service.sql.schema.DatabaseSchemaInitializer;

/**
 * A base class with default implementation/template for all Hussar Docker {@link BaseDatabaseDockerService}.
 *
 * @param <C> the {@link SQLDatabaseDockerServiceConfig} parameter used by the {@link BaseDatabaseDockerService}.
 */
abstract class BaseDatabaseDockerService<C extends SQLDatabaseDockerServiceConfig> extends BaseDockerService<C> implements SQLDatabaseDockerService {
    private final SQLDatabaseCredentials credentials;
    private final DatabaseCredentialsRegisterer credentialsRegisterer;
    private final DatabaseSchemaInitializer schemaInitializer;

    /**
     * Creates new {@link BaseDatabaseDockerService}.
     *
     * @param container - the {@link GenericContainer} used by this {@link BaseDatabaseDockerService}.
     * @param config - the configuration <C> used by this {@link BaseDatabaseDockerService}.
     * @param configurationRegistry - the {@link ConfigurationRegistry} used by this {@link BaseDatabaseDockerService}.
     * @param endpointRegisterer - the  {@link EndpointRegisterer} used by this {@link BaseDatabaseDockerService}.
     * @param credentialsRegisterer - the {@link DatabaseCredentialsRegisterer} used by this {@link BaseDatabaseDockerService}.
     * @param credentials - the {@link SQLDatabaseCredentials} used by this {@link BaseDatabaseDockerService}.
     * @param schemaInitializer - the {@link DatabaseSchemaInitializer} used by this {@link BaseDatabaseDockerService}.
     */
    BaseDatabaseDockerService(@NonNull GenericContainer<?> container,
                              @NonNull C config,
                              @NonNull ConfigurationRegistry configurationRegistry,
                              @NonNull EndpointRegisterer endpointRegisterer,
                              @NonNull DatabaseCredentialsRegisterer credentialsRegisterer,
                              @NonNull SQLDatabaseCredentials credentials,
                              @NonNull DatabaseSchemaInitializer schemaInitializer) {
        super(container, config, configurationRegistry, endpointRegisterer);

        this.credentialsRegisterer = credentialsRegisterer;
        this.credentials = credentials;
        this.schemaInitializer = schemaInitializer;
    }

    @Override
    protected void doAfterServiceStartup(ServiceStartupContext context) {
        super.doAfterServiceStartup(context);

        registerCredentialsUnderProperties();
        registerCredentialsUnderEnvironmentVariables();

        initializeDatabaseSchemas();
    }

    @Override
    public SQLDatabaseCredentials getCredentials() {
        return credentials;
    }

    private void registerCredentialsUnderProperties() {
        config.getRegisterUsernameUnderProperties()
              .forEach(usernameProperty -> credentialsRegisterer.registerUsernameUnderProperty(credentials, usernameProperty));

        config.getRegisterPasswordUnderProperties()
              .forEach(passwordProperty -> credentialsRegisterer.registerPasswordUnderProperty(credentials, passwordProperty));
    }

    private void registerCredentialsUnderEnvironmentVariables() {
        config.getRegisterUsernameUnderEnvironmentVariables()
              .forEach(usernameEnvVariable -> credentialsRegisterer.registerUsernameUnderEnvironmentVariable(credentials, usernameEnvVariable));

        config.getRegisterPasswordUnderEnvironmentVariables()
              .forEach(passwordEnvVariable -> credentialsRegisterer.registerPasswordUnderEnvironmentVariable(credentials, passwordEnvVariable));
    }

    private void initializeDatabaseSchemas() {
        var endpoint = EndpointHelper.getAnyEndpointOrFail(this);

        config.getDatabaseSchemas()
              .forEach(schema -> schemaInitializer.initialize(endpoint, credentials, schema));
    }

}
