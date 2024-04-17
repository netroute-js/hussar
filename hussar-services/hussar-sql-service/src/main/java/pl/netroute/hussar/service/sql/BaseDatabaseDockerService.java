package pl.netroute.hussar.service.sql;

import lombok.NonNull;
import org.testcontainers.containers.GenericContainer;
import pl.netroute.hussar.core.api.ConfigurationRegistry;
import pl.netroute.hussar.core.api.ServiceStartupContext;
import pl.netroute.hussar.core.helper.EndpointHelper;
import pl.netroute.hussar.core.service.BaseDockerService;
import pl.netroute.hussar.core.service.registerer.EndpointRegisterer;
import pl.netroute.hussar.service.sql.api.SQLDatabaseCredentials;
import pl.netroute.hussar.service.sql.registerer.DatabaseCredentialsRegisterer;
import pl.netroute.hussar.service.sql.schema.DatabaseSchemaInitializer;

abstract class BaseDatabaseDockerService<C extends SQLDatabaseDockerServiceConfig> extends BaseDockerService<C> implements SQLDatabaseDockerService {
    private final SQLDatabaseCredentials credentials;
    private final DatabaseCredentialsRegisterer credentialsRegisterer;
    private final DatabaseSchemaInitializer schemaInitializer;

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
