package pl.netroute.hussar.service.nosql.redis.api;

import lombok.NonNull;
import org.testcontainers.containers.GenericContainer;
import pl.netroute.hussar.core.configuration.api.ConfigurationRegistry;
import pl.netroute.hussar.core.service.api.Service;
import pl.netroute.hussar.core.service.ServiceStartupContext;
import pl.netroute.hussar.core.service.api.BaseDockerService;
import pl.netroute.hussar.core.service.registerer.EndpointRegisterer;

/**
 * Hussar Docker {@link Service} representing Redis.
 */
public class RedisDockerService extends BaseDockerService<RedisDockerServiceConfig> {
    private static final int LISTENING_PORT = 6379;

    private static final String REDIS_USERNAME = "default";
    private static final String REDIS_PASSWORD = "test";

    private final RedisCredentials credentials;
    private final RedisCredentialsRegisterer credentialsRegisterer;
    private final RedisPasswordConfigurer passwordConfigurer;

    /**
     * Creates new {@link RedisDockerService}.
     *
     * @param container - the {@link GenericContainer} used by this {@link RedisDockerService}.
     * @param config - the {@link RedisDockerServiceConfig} used by this {@link RedisDockerService}.
     * @param configurationRegistry - the {@link ConfigurationRegistry} used by this {@link RedisDockerService}.
     * @param endpointRegisterer - the  {@link EndpointRegisterer} used by this {@link RedisDockerService}.
     * @param credentialsRegisterer - the {@link RedisCredentialsRegisterer} used by this {@link RedisDockerService}.
     * @param passwordConfigurer - the {@link RedisPasswordConfigurer} used by this {@link RedisDockerService}.
     */
    RedisDockerService(@NonNull GenericContainer<?> container,
                       @NonNull RedisDockerServiceConfig config,
                       @NonNull ConfigurationRegistry configurationRegistry,
                       @NonNull EndpointRegisterer endpointRegisterer,
                       @NonNull RedisCredentialsRegisterer credentialsRegisterer,
                       @NonNull RedisPasswordConfigurer passwordConfigurer) {
        super(container, config, configurationRegistry, endpointRegisterer);

        if(isPasswordEnabled()) {
            this.credentials = new RedisCredentials(REDIS_USERNAME, REDIS_PASSWORD);
        } else {
            this.credentials = RedisCredentials.passwordLess(REDIS_USERNAME);
        }

        this.credentialsRegisterer = credentialsRegisterer;
        this.passwordConfigurer = passwordConfigurer;
    }

    @Override
    protected void configureContainer(GenericContainer<?> container) {
        super.configureContainer(container);

        container.withExposedPorts(LISTENING_PORT);
    }

    @Override
    protected void doAfterServiceStartup(ServiceStartupContext context) {
        super.doAfterServiceStartup(context);

        if(isPasswordEnabled()) {
            passwordConfigurer.configure(credentials, container);
        }

        registerCredentialsUnderProperties();
        registerCredentialsUnderEnvironmentVariables();
    }

    /**
     * Returns {@link RedisCredentials}.
     *
     * @return the actual {@link RedisCredentials}.
     */
    public RedisCredentials getCredentials() {
        return credentials;
    }

    private boolean isPasswordEnabled() {
        return config.isEnablePassword();
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
