package pl.netroute.hussar.service.nosql.redis.api;

import lombok.Getter;
import lombok.NonNull;
import org.testcontainers.containers.GenericContainer;
import pl.netroute.hussar.core.configuration.api.ConfigurationRegistry;
import pl.netroute.hussar.core.network.api.NetworkConfigurer;
import pl.netroute.hussar.core.service.api.Service;
import pl.netroute.hussar.core.service.ServiceStartupContext;
import pl.netroute.hussar.core.service.api.BaseDockerService;
import pl.netroute.hussar.core.service.registerer.EndpointRegisterer;

import static pl.netroute.hussar.service.nosql.redis.api.RedisSettings.REDIS_LISTENING_PORT;
import static pl.netroute.hussar.service.nosql.redis.api.RedisSettings.REDIS_PASSWORD;
import static pl.netroute.hussar.service.nosql.redis.api.RedisSettings.REDIS_USERNAME;

/**
 * Hussar Docker {@link Service} representing Redis.
 */
public class RedisDockerService extends BaseDockerService<RedisDockerServiceConfig> {

    @Getter
    @NonNull
    private final RedisCredentials credentials;

    @NonNull
    private final RedisCredentialsRegisterer credentialsRegisterer;

    @NonNull
    private final RedisPasswordConfigurer passwordConfigurer;

    /**
     * Creates new {@link RedisDockerService}.
     *
     * @param container - the {@link GenericContainer} used by this {@link RedisDockerService}.
     * @param config - the {@link RedisDockerServiceConfig} used by this {@link RedisDockerService}.
     * @param configurationRegistry - the {@link ConfigurationRegistry} used by this {@link RedisDockerService}.
     * @param endpointRegisterer - the  {@link EndpointRegisterer} used by this {@link RedisDockerService}.
     * @param networkConfigurer - the  {@link NetworkConfigurer} used by this {@link RedisDockerService}.
     * @param credentialsRegisterer - the {@link RedisCredentialsRegisterer} used by this {@link RedisDockerService}.
     * @param passwordConfigurer - the {@link RedisPasswordConfigurer} used by this {@link RedisDockerService}.
     */
    RedisDockerService(@NonNull GenericContainer<?> container,
                       @NonNull RedisDockerServiceConfig config,
                       @NonNull ConfigurationRegistry configurationRegistry,
                       @NonNull EndpointRegisterer endpointRegisterer,
                       @NonNull NetworkConfigurer networkConfigurer,
                       @NonNull RedisCredentialsRegisterer credentialsRegisterer,
                       @NonNull RedisPasswordConfigurer passwordConfigurer) {
        super(container, config, configurationRegistry, endpointRegisterer, networkConfigurer);

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

        container.withExposedPorts(REDIS_LISTENING_PORT);
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
