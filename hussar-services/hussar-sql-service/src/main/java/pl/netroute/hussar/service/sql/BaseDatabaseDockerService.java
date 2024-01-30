package pl.netroute.hussar.service.sql;

import lombok.NonNull;
import org.flywaydb.core.internal.jdbc.DriverDataSource;
import pl.netroute.hussar.core.api.ServiceStartupContext;
import pl.netroute.hussar.core.helper.EndpointHelper;
import pl.netroute.hussar.core.service.BaseDockerService;

import javax.sql.DataSource;
import java.util.Optional;

abstract class BaseDatabaseDockerService<C extends BaseDatabaseDockerServiceConfig> extends BaseDockerService<C> implements DatabaseDockerService {
    private static final String UNKNOWN_DRIVER = null;

    private final DatabaseCredentials credentials;
    private final DatabaseCredentialsRegisterer credentialsRegisterer;

    BaseDatabaseDockerService(@NonNull C config,
                              @NonNull DatabaseCredentials credentials) {
        super(config);

        this.credentials = credentials;
        this.credentialsRegisterer = new DatabaseCredentialsRegisterer(configurationRegistry);
    }

    @Override
    protected void doAfterServiceStartup(ServiceStartupContext context) {
        super.doAfterServiceStartup(context);

        var dataSource = createDataSource();

        registerCredentialsUnderProperties();
        registerCredentialsUnderEnvironmentVariables();

        initializeDatabaseSchemas(dataSource);
    }

    @Override
    public DatabaseCredentials getCredentials() {
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

    private void initializeDatabaseSchemas(DataSource dataSource) {
        var databaseSchemaInitializer = new DatabaseSchemaInitializer(dataSource);

        config.getDatabaseSchemas()
              .forEach(databaseSchemaInitializer::initialize);
    }

    private DataSource createDataSource() {
        var classLoader = Thread.currentThread().getContextClassLoader();

        var jdbcURL = EndpointHelper
                .getAnyEndpointOrFail(this)
                .getAddress();

        var username = credentials.username();
        var password = credentials.password();

        return new DriverDataSource(classLoader, UNKNOWN_DRIVER, jdbcURL, username, password);
    }

}
