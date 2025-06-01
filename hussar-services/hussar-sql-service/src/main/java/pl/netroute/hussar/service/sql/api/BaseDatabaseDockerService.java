package pl.netroute.hussar.service.sql.api;

import lombok.Getter;
import lombok.NonNull;
import org.testcontainers.containers.GenericContainer;
import pl.netroute.hussar.core.configuration.api.ConfigurationRegistry;
import pl.netroute.hussar.core.docker.api.DockerNetwork;
import pl.netroute.hussar.core.network.api.NetworkConfigurer;
import pl.netroute.hussar.core.service.ServiceStartupContext;
import pl.netroute.hussar.core.helper.EndpointHelper;
import pl.netroute.hussar.core.service.api.BaseDockerService;
import pl.netroute.hussar.core.service.registerer.EndpointRegisterer;
import pl.netroute.hussar.service.sql.schema.DatabaseSchemaInitializer;

/**
 * A base class with default implementation/template for all Hussar Docker {@link BaseDatabaseDockerService}.
 *
 * @param <C> the {@link SQLDatabaseDockerServiceConfig} parameter used by the {@link BaseDatabaseDockerService}.
 */
abstract class BaseDatabaseDockerService<C extends SQLDatabaseDockerServiceConfig> extends BaseDockerService<C> implements SQLDatabaseDockerService {

    @Getter
    @NonNull
    private final SQLDatabaseCredentials credentials;

    @NonNull
    private final DatabaseCredentialsRegisterer credentialsRegisterer;

    @NonNull
    private final DatabaseSchemaInitializer schemaInitializer;

    /**
     * Creates new {@link BaseDatabaseDockerService}.
     *
     * @param container - the {@link GenericContainer} used by this {@link BaseDatabaseDockerService}.
     * @param dockerNetwork - the {@link DockerNetwork} used by this {@link BaseDatabaseDockerService}.
     * @param config - the configuration <C> used by this {@link BaseDatabaseDockerService}.
     * @param configurationRegistry - the {@link ConfigurationRegistry} used by this {@link BaseDatabaseDockerService}.
     * @param endpointRegisterer - the  {@link EndpointRegisterer} used by this {@link BaseDatabaseDockerService}.
     * @param networkConfigurer - the  {@link NetworkConfigurer} used by this {@link BaseDatabaseDockerService}.
     * @param credentialsRegisterer - the {@link DatabaseCredentialsRegisterer} used by this {@link BaseDatabaseDockerService}.
     * @param credentials - the {@link SQLDatabaseCredentials} used by this {@link BaseDatabaseDockerService}.
     * @param schemaInitializer - the {@link DatabaseSchemaInitializer} used by this {@link BaseDatabaseDockerService}.
     */
    BaseDatabaseDockerService(@NonNull GenericContainer<?> container,
                              @NonNull DockerNetwork dockerNetwork,
                              @NonNull C config,
                              @NonNull ConfigurationRegistry configurationRegistry,
                              @NonNull EndpointRegisterer endpointRegisterer,
                              @NonNull NetworkConfigurer networkConfigurer,
                              @NonNull DatabaseCredentialsRegisterer credentialsRegisterer,
                              @NonNull SQLDatabaseCredentials credentials,
                              @NonNull DatabaseSchemaInitializer schemaInitializer) {
        super(container, dockerNetwork, config, configurationRegistry, endpointRegisterer, networkConfigurer);

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
