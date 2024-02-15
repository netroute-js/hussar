package pl.netroute.hussar.service.sql;

import lombok.NonNull;
import pl.netroute.hussar.core.api.ServiceStartupContext;
import pl.netroute.hussar.core.helper.EndpointHelper;
import pl.netroute.hussar.core.service.BaseDockerService;
import pl.netroute.hussar.service.sql.api.SQLDatabaseCredentials;

import java.util.Optional;

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
        Optional
                .ofNullable(config.getRegisterUsernameUnderProperty())
                .ifPresent(usernameProperty -> credentialsRegisterer.registerUsernameUnderProperty(credentials, usernameProperty));

        Optional
                .ofNullable(config.getRegisterUsernameUnderEnvironmentVariable())
                .ifPresent(usernameEnvVariable -> credentialsRegisterer.registerUsernameUnderEnvironmentVariable(credentials, usernameEnvVariable));
    }

    private void registerCredentialsUnderEnvironmentVariables() {
        Optional
                .ofNullable(config.getRegisterPasswordUnderProperty())
                .ifPresent(passwordProperty -> credentialsRegisterer.registerPasswordUnderProperty(credentials, passwordProperty));

        Optional
                .ofNullable(config.getRegisterPasswordUnderEnvironmentVariable())
                .ifPresent(passwordEnvVariable -> credentialsRegisterer.registerPasswordUnderEnvironmentVariable(credentials, passwordEnvVariable));
    }

    private void initializeDatabaseSchemas() {
        var endpoint = EndpointHelper.getAnyEndpointOrFail(this);
        var databaseSchemaInitializer = new DatabaseSchemaInitializer(endpoint, credentials);

        config.getDatabaseSchemas()
              .forEach(databaseSchemaInitializer::initialize);
    }

}
