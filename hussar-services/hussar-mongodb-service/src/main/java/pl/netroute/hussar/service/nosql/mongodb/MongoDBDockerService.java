package pl.netroute.hussar.service.nosql.mongodb;

import lombok.NonNull;
import org.testcontainers.containers.GenericContainer;
import pl.netroute.hussar.core.api.ServiceStartupContext;
import pl.netroute.hussar.core.service.BaseDockerService;
import pl.netroute.hussar.service.nosql.mongodb.api.MongoDBCredentials;

import java.util.Optional;

public class MongoDBDockerService extends BaseDockerService<MongoDBDockerServiceConfig> {
    private static final int LISTENING_PORT = 27017;

    private static final String MONGO_DB_USERNAME_ENV = "MONGO_INITDB_ROOT_USERNAME";
    private static final String MONGO_DB_PASSWORD_ENV = "MONGO_INITDB_ROOT_PASSWORD";

    private static final String MONGO_DB_USERNAME = "mongo";
    private static final String MONGO_DB_PASSWORD = "test";

    private final MongoDBCredentials credentials;
    private final DatabaseCredentialsRegisterer credentialsRegisterer;

    MongoDBDockerService(@NonNull MongoDBDockerServiceConfig config) {
        super(config);

        this.credentials = new MongoDBCredentials(MONGO_DB_USERNAME, MONGO_DB_PASSWORD);
        this.credentialsRegisterer = new DatabaseCredentialsRegisterer(configurationRegistry);
    }

    @Override
    protected void configureContainer(GenericContainer<?> container) {
        super.configureContainer(container);

        container
                .withExposedPorts(LISTENING_PORT)
                .withEnv(MONGO_DB_USERNAME_ENV, MONGO_DB_USERNAME)
                .withEnv(MONGO_DB_PASSWORD_ENV, MONGO_DB_PASSWORD);
    }

    @Override
    protected void doAfterServiceStartup(ServiceStartupContext context) {
        super.doAfterServiceStartup(context);

        registerCredentialsUnderProperties();
        registerCredentialsUnderEnvironmentVariables();
    }

    public MongoDBCredentials getCredentials() {
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
}
