package pl.netroute.hussar.service.nosql.mongodb;

import lombok.NonNull;
import org.testcontainers.containers.GenericContainer;
import pl.netroute.hussar.core.api.configuration.ConfigurationRegistry;
import pl.netroute.hussar.core.api.service.Service;
import pl.netroute.hussar.core.api.service.ServiceStartupContext;
import pl.netroute.hussar.core.service.BaseDockerService;
import pl.netroute.hussar.core.service.registerer.EndpointRegisterer;
import pl.netroute.hussar.service.nosql.mongodb.api.MongoDBCredentials;

/**
 * Hussar Docker {@link Service} representing MongoDB.
 */
public class MongoDBDockerService extends BaseDockerService<MongoDBDockerServiceConfig> {
    private static final int LISTENING_PORT = 27017;

    private static final String MONGO_DB_USERNAME_ENV = "MONGO_INITDB_ROOT_USERNAME";
    private static final String MONGO_DB_PASSWORD_ENV = "MONGO_INITDB_ROOT_PASSWORD";

    private static final String MONGO_DB_USERNAME = "mongo";
    private static final String MONGO_DB_PASSWORD = "test";

    private final MongoDBCredentials credentials;
    private final MongoDBCredentialsRegisterer credentialsRegisterer;

    /**
     * Creates new MongoDB {@link MongoDBDockerService}.
     *
     * @param container - the {@link GenericContainer} used by this {@link MongoDBDockerService}.
     * @param config - the {@link MongoDBDockerServiceConfig} used by this {@link MongoDBDockerService}.
     * @param configurationRegistry - the {@link ConfigurationRegistry} used by this {@link MongoDBDockerService}.
     * @param endpointRegisterer - the  {@link EndpointRegisterer} used by this {@link MongoDBDockerService}.
     * @param credentialsRegisterer - the {@link MongoDBCredentialsRegisterer} used by this {@link MongoDBDockerService}.
     */
    MongoDBDockerService(@NonNull GenericContainer<?> container,
                         @NonNull MongoDBDockerServiceConfig config,
                         @NonNull ConfigurationRegistry configurationRegistry,
                         @NonNull EndpointRegisterer endpointRegisterer,
                         @NonNull MongoDBCredentialsRegisterer credentialsRegisterer) {
        super(container, config, configurationRegistry, endpointRegisterer);

        this.credentials = new MongoDBCredentials(MONGO_DB_USERNAME, MONGO_DB_PASSWORD);
        this.credentialsRegisterer = credentialsRegisterer;
    }

    @Override
    protected void configureContainer(GenericContainer<?> container) {
        super.configureContainer(container);

        container.withExposedPorts(LISTENING_PORT);
        container.withEnv(MONGO_DB_USERNAME_ENV, MONGO_DB_USERNAME);
        container.withEnv(MONGO_DB_PASSWORD_ENV, MONGO_DB_PASSWORD);
    }

    @Override
    protected void doAfterServiceStartup(ServiceStartupContext context) {
        super.doAfterServiceStartup(context);

        registerCredentialsUnderProperties();
        registerCredentialsUnderEnvironmentVariables();
    }

    /**
     * Returns {@link MongoDBCredentials}.
     *
     * @return the actual {@link MongoDBCredentials}
     */
    public MongoDBCredentials getCredentials() {
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
}
