package pl.netroute.hussar.junit5.factory;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.netroute.hussar.service.nosql.redis.api.RedisDockerServiceConfigConfigurer;

import static pl.netroute.hussar.junit5.config.ApplicationEnvironmentVariables.REDIS_PASSWORD_ENV_VARIABLE;
import static pl.netroute.hussar.junit5.config.ApplicationEnvironmentVariables.REDIS_URL_ENV_VARIABLE;
import static pl.netroute.hussar.junit5.config.ApplicationEnvironmentVariables.REDIS_USERNAME_ENV_VARIABLE;
import static pl.netroute.hussar.junit5.config.ApplicationProperties.REDIS_ALTERNATIVE_PASSWORD_PROPERTY;
import static pl.netroute.hussar.junit5.config.ApplicationProperties.REDIS_ALTERNATIVE_URL_PROPERTY;
import static pl.netroute.hussar.junit5.config.ApplicationProperties.REDIS_ALTERNATIVE_USERNAME_PROPERTY;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RedisServiceFactory {
    public static final String REDIS_NAME = "redis-instance";

    public static RedisDockerServiceConfigConfigurer create() {
        var dockerImageVersion = "7.2.5";

        return RedisDockerServiceConfigConfigurer
                .newInstance()
                .name(REDIS_NAME)
                .dockerImageVersion(dockerImageVersion)
                .enablePassword(true)
                .registerEndpointUnderProperty(REDIS_ALTERNATIVE_URL_PROPERTY)
                .registerEndpointUnderEnvironmentVariable(REDIS_URL_ENV_VARIABLE)
                .registerUsernameUnderProperty(REDIS_ALTERNATIVE_USERNAME_PROPERTY)
                .registerUsernameUnderEnvironmentVariable(REDIS_USERNAME_ENV_VARIABLE)
                .registerPasswordUnderProperty(REDIS_ALTERNATIVE_PASSWORD_PROPERTY)
                .registerPasswordUnderEnvironmentVariable(REDIS_PASSWORD_ENV_VARIABLE)
                .done();
    }

}
