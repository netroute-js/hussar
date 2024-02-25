package pl.netroute.hussar.service.sql;

import lombok.NonNull;
import pl.netroute.hussar.core.api.ServiceStartupContext;
import pl.netroute.hussar.core.helper.CollectionHelper;
import pl.netroute.hussar.core.helper.EndpointHelper;
import pl.netroute.hussar.core.service.BaseDockerService;
import pl.netroute.hussar.service.sql.api.SQLDatabaseCredentials;

abstract class BaseDatabaseDockerService<C extends SQLDatabaseDockerServiceConfig> extends BaseDockerService<C> implements SQLDatabaseDockerService {
    private final SQLDatabaseCredentials credentials;
    private final DatabaseCredentialsRegisterer credentialsRegisterer;

    BaseDatabaseDockerService(@NonNull C config,
                              @NonNull SQLDatabaseCredentials credentials) {
        super(config);

        this.credentials = credentials;
        this.credentialsRegisterer = new DatabaseCredentialsRegisterer(configurationRegistry);
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
        CollectionHelper
                .getSetOrEmpty(config.getRegisterUsernameUnderProperties())
                .forEach(usernameProperty -> credentialsRegisterer.registerUsernameUnderProperty(credentials, usernameProperty));

        CollectionHelper
                .getSetOrEmpty(config.getRegisterPasswordUnderProperties())
                .forEach(passwordProperty -> credentialsRegisterer.registerPasswordUnderProperty(credentials, passwordProperty));
    }

    private void registerCredentialsUnderEnvironmentVariables() {
        CollectionHelper
                .getSetOrEmpty(config.getRegisterUsernameUnderEnvironmentVariables())
                .forEach(usernameEnvVariable -> credentialsRegisterer.registerUsernameUnderEnvironmentVariable(credentials, usernameEnvVariable));

        CollectionHelper
                .getSetOrEmpty(config.getRegisterPasswordUnderEnvironmentVariables())
                .forEach(passwordEnvVariable -> credentialsRegisterer.registerPasswordUnderEnvironmentVariable(credentials, passwordEnvVariable));
    }

    private void initializeDatabaseSchemas() {
        var endpoint = EndpointHelper.getAnyEndpointOrFail(this);
        var databaseSchemaInitializer = new DatabaseSchemaInitializer(endpoint, credentials);

        config.getDatabaseSchemas()
              .forEach(databaseSchemaInitializer::initialize);
    }

}
