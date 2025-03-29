package pl.netroute.hussar.junit5.factory;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.netroute.hussar.service.nosql.redis.api.RedisClusterDockerServiceConfigurer;

import static pl.netroute.hussar.junit5.config.ApplicationEnvironmentVariables.REDIS_CLUSTER_PASSWORD_ENV_VARIABLE;
import static pl.netroute.hussar.junit5.config.ApplicationEnvironmentVariables.REDIS_CLUSTER_URL_ENV_VARIABLE;
import static pl.netroute.hussar.junit5.config.ApplicationEnvironmentVariables.REDIS_CLUSTER_USERNAME_ENV_VARIABLE;
import static pl.netroute.hussar.junit5.config.ApplicationProperties.REDIS_CLUSTER_ALTERNATIVE_PASSWORD_PROPERTY;
import static pl.netroute.hussar.junit5.config.ApplicationProperties.REDIS_CLUSTER_ALTERNATIVE_URL_PROPERTY;
import static pl.netroute.hussar.junit5.config.ApplicationProperties.REDIS_CLUSTER_ALTERNATIVE_USERNAME_PROPERTY;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RedisClusterServiceFactory {
    public static final String REDIS_CLUSTER_NAME = "redis-cluster-instance";

    public static RedisClusterDockerServiceConfigurer create() {
        var dockerImageVersion = "6.2.0";

        return RedisClusterDockerServiceConfigurer
                .newInstance()
                .name(REDIS_CLUSTER_NAME)
                .dockerImageVersion(dockerImageVersion)
                .enablePassword(true)
                .registerEndpointUnderProperty(REDIS_CLUSTER_ALTERNATIVE_URL_PROPERTY)
                .registerEndpointUnderEnvironmentVariable(REDIS_CLUSTER_URL_ENV_VARIABLE)
                .registerUsernameUnderProperty(REDIS_CLUSTER_ALTERNATIVE_USERNAME_PROPERTY)
                .registerUsernameUnderEnvironmentVariable(REDIS_CLUSTER_USERNAME_ENV_VARIABLE)
                .registerPasswordUnderProperty(REDIS_CLUSTER_ALTERNATIVE_PASSWORD_PROPERTY)
                .registerPasswordUnderEnvironmentVariable(REDIS_CLUSTER_PASSWORD_ENV_VARIABLE)
                .done();
    }

}
