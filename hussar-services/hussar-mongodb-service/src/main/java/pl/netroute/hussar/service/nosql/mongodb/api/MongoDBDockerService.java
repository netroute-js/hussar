package pl.netroute.hussar.service.nosql.mongodb.api;

import lombok.Getter;
import lombok.NonNull;
import org.testcontainers.containers.GenericContainer;
import pl.netroute.hussar.core.configuration.api.ConfigurationRegistry;
import pl.netroute.hussar.core.docker.api.DockerNetwork;
import pl.netroute.hussar.core.network.api.NetworkConfigurer;
import pl.netroute.hussar.core.service.api.Service;
import pl.netroute.hussar.core.service.ServiceStartupContext;
import pl.netroute.hussar.core.service.api.BaseDockerService;
import pl.netroute.hussar.core.service.registerer.EndpointRegisterer;

import java.util.List;

/**
 * Hussar Docker {@link Service} representing MongoDB.
 */
public class MongoDBDockerService extends BaseDockerService<MongoDBDockerServiceConfig> {
    private static final int LISTENING_PORT = 27017;

    private static final String MONGO_DB_USERNAME_ENV = "MONGO_INITDB_ROOT_USERNAME";
    private static final String MONGO_DB_PASSWORD_ENV = "MONGO_INITDB_ROOT_PASSWORD";

    private static final String MONGO_DB_USERNAME = "mongo";
    private static final String MONGO_DB_PASSWORD = "test";

    @Getter
    @NonNull
    private final MongoDBCredentials credentials;

    @NonNull
    private final MongoDBEndpointWithCredentialsRegisterer endpointWithCredentialsRegisterer;

    @NonNull
    private final MongoDBCredentialsRegisterer credentialsRegisterer;

    /**
     * Creates new MongoDB {@link MongoDBDockerService}.
     *
     * @param container - the {@link GenericContainer} used by this {@link MongoDBDockerService}.
     * @param dockerNetwork - the {@link DockerNetwork} used by this {@link MongoDBDockerService}.
     * @param config - the {@link MongoDBDockerServiceConfig} used by this {@link MongoDBDockerService}.
     * @param configurationRegistry - the {@link ConfigurationRegistry} used by this {@link MongoDBDockerService}.
     * @param endpointRegisterer - the  {@link EndpointRegisterer} used by this {@link MongoDBDockerService}.
     * @param networkConfigurer - the  {@link NetworkConfigurer} used by this {@link MongoDBDockerService}.
     * @param endpointWithCredentialsRegisterer - the  {@link MongoDBEndpointWithCredentialsRegisterer} used by this {@link MongoDBDockerService}.
     * @param credentialsRegisterer - the {@link MongoDBCredentialsRegisterer} used by this {@link MongoDBDockerService}.
     */
    MongoDBDockerService(@NonNull GenericContainer<?> container,
                         @NonNull DockerNetwork dockerNetwork,
                         @NonNull MongoDBDockerServiceConfig config,
                         @NonNull ConfigurationRegistry configurationRegistry,
                         @NonNull EndpointRegisterer endpointRegisterer,
                         @NonNull NetworkConfigurer networkConfigurer,
                         @NonNull MongoDBEndpointWithCredentialsRegisterer endpointWithCredentialsRegisterer,
                         @NonNull MongoDBCredentialsRegisterer credentialsRegisterer) {
        super(container, dockerNetwork, config, configurationRegistry, endpointRegisterer, networkConfigurer);

        this.endpointWithCredentialsRegisterer = endpointWithCredentialsRegisterer;
        this.credentials = defaultCredentials();
        this.credentialsRegisterer = credentialsRegisterer;
    }

    @Override
    protected void configureEnvVariables(GenericContainer<?> container) {
        super.configureEnvVariables(container);

        container.withEnv(MONGO_DB_USERNAME_ENV, MONGO_DB_USERNAME);
        container.withEnv(MONGO_DB_PASSWORD_ENV, MONGO_DB_PASSWORD);
    }

    @Override
    protected void doAfterServiceStartup(ServiceStartupContext context) {
        super.doAfterServiceStartup(context);

        registerEndpointWithCredentialsUnderProperties();
        registerEndpointWithCredentialsUnderEnvironmentVariables();

        registerCredentialsUnderProperties();
        registerCredentialsUnderEnvironmentVariables();
    }

    @Override
    protected List<Integer> getInternalPorts() {
        return List.of(LISTENING_PORT);
    }

    private void registerEndpointWithCredentialsUnderProperties() {
        var endpoints = getEndpoints();

        config.getRegisterEndpointWithCredentialsUnderProperties()
              .forEach(endpointProperty -> endpointWithCredentialsRegisterer.registerUnderProperty(endpoints, credentials, endpointProperty));
    }

    private void registerEndpointWithCredentialsUnderEnvironmentVariables() {
        var endpoints = getEndpoints();

        config.getRegisterEndpointWithCredentialsUnderEnvironmentVariables()
              .forEach(endpointEnvVariable -> endpointWithCredentialsRegisterer.registerUnderEnvironmentVariable(endpoints, credentials, endpointEnvVariable));
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

    private static MongoDBCredentials defaultCredentials() {
        return new MongoDBCredentials(MONGO_DB_USERNAME, MONGO_DB_PASSWORD);
    }
}
