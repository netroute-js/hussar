package pl.netroute.hussar.service.nosql.redis;

import lombok.NonNull;
import org.testcontainers.containers.GenericContainer;
import pl.netroute.hussar.core.api.ServiceStartupContext;
import pl.netroute.hussar.core.helper.CollectionHelper;
import pl.netroute.hussar.core.service.BaseDockerService;
import pl.netroute.hussar.service.nosql.redis.api.RedisCredentials;

public class RedisDockerService extends BaseDockerService<RedisDockerServiceConfig> {
    private static final int LISTENING_PORT = 6379;

    private static final String REDIS_USERNAME = "default";
    private static final String REDIS_PASSWORD = "test";

    private final RedisCredentials credentials;
    private final RedisCredentialsRegisterer credentialsRegisterer;

    RedisDockerService(@NonNull RedisDockerServiceConfig config) {
        super(config);

        if(isPasswordEnabled()) {
            this.credentials = new RedisCredentials(REDIS_USERNAME, REDIS_PASSWORD);
        } else {
            this.credentials = RedisCredentials.passwordLess(REDIS_USERNAME);
        }

        this.credentialsRegisterer = new RedisCredentialsRegisterer(configurationRegistry);
    }

    @Override
    protected void configureContainer(GenericContainer<?> container) {
        super.configureContainer(container);

        container
                .withExposedPorts(LISTENING_PORT);
    }

    @Override
    protected void doAfterServiceStartup(ServiceStartupContext context) {
        super.doAfterServiceStartup(context);

        if(isPasswordEnabled()) {
            new RedisPasswordConfigurer(container).configure(credentials);
        }

        registerCredentialsUnderProperties();
        registerCredentialsUnderEnvironmentVariables();
    }

    public RedisCredentials getCredentials() {
        return credentials;
    }

    private boolean isPasswordEnabled() {
        return config.isEnablePassword();
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

}
