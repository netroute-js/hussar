package pl.netroute.hussar.service.nosql.redis;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import pl.netroute.hussar.core.service.ServiceConfigureContext;
import pl.netroute.hussar.core.service.ServiceStartupContext;
import pl.netroute.hussar.service.nosql.redis.api.RedisClusterDockerService;
import pl.netroute.hussar.service.nosql.redis.api.RedisClusterDockerServiceConfigurer;
import pl.netroute.hussar.service.nosql.redis.assertion.RedisClusterAssertionHelper;

import java.util.Optional;

@Slf4j
public class RedisClusterDockerServiceIT {
    private RedisClusterDockerService redisClusterService;

    @AfterEach
    public void cleanup() {
        Optional
                .ofNullable(redisClusterService)
                .ifPresent(RedisClusterDockerService::shutdown);
    }

    @Test
    public void shouldStartRedisClusterService() {
        // given
        redisClusterService = RedisClusterDockerServiceConfigurer
                .newInstance()
                .enablePassword(true)
                .done()
                .configure(ServiceConfigureContext.defaultContext());

        // when
        redisClusterService.start(ServiceStartupContext.defaultContext());

        // then
        var redisClusterAssertion = new RedisClusterAssertionHelper(redisClusterService);
        redisClusterAssertion.assertMultipleEndpoints();
        redisClusterAssertion.asserRedisClusterAccessible();
        redisClusterAssertion.assertNoEntriesRegistered();
    }

    @Test
    public void shouldStartSecuredRedisClusterService() {
        // given
        redisClusterService = RedisClusterDockerServiceConfigurer
                .newInstance()
                .enablePassword(true)
                .done()
                .configure(ServiceConfigureContext.defaultContext());

        // when
        redisClusterService.start(ServiceStartupContext.defaultContext());

        // then
        var redisClusterAssertion = new RedisClusterAssertionHelper(redisClusterService);
        redisClusterAssertion.assertMultipleEndpoints();
        redisClusterAssertion.asserRedisClusterAccessible();
        redisClusterAssertion.assertNoEntriesRegistered();
    }

    @Test
    public void shouldStartRedisClusterServiceWithFullConfiguration() {
        // given
        var name = "redis-cluster-instance";
        var dockerVersion = "6.2.0";

        var endpointProperty = "redis.cluster.url";
        var endpointEnvVariable = "REDIS_CLUSTER_URL";

        var usernameProperty = "redis.cluster.username";
        var usernameEnvVariable = "REDIS_CLUSTER_USERNAME";

        var passwordProperty = "redis.cluster.password";
        var passwordEnvVariable = "REDIS_CLUSTER_PASSWORD";

        redisClusterService = RedisClusterDockerServiceConfigurer
                .newInstance()
                .name(name)
                .dockerImageVersion(dockerVersion)
                .enablePassword(true)
                .registerEndpointUnderProperty(endpointProperty)
                .registerEndpointUnderEnvironmentVariable(endpointEnvVariable)
                .registerUsernameUnderProperty(usernameProperty)
                .registerUsernameUnderEnvironmentVariable(usernameEnvVariable)
                .registerPasswordUnderProperty(passwordProperty)
                .registerPasswordUnderEnvironmentVariable(passwordEnvVariable)
                .done()
                .configure(ServiceConfigureContext.defaultContext());

        // when
        redisClusterService.start(ServiceStartupContext.defaultContext());

        // then
        var redisClusterAssertion = new RedisClusterAssertionHelper(redisClusterService);
        redisClusterAssertion.assertMultipleEndpoints();
        redisClusterAssertion.asserRedisClusterAccessible();
        redisClusterAssertion.assertRegisteredEndpointsUnderProperty(endpointProperty);
        redisClusterAssertion.assertRegisteredEndpointsUnderEnvironmentVariable(endpointEnvVariable);
        redisClusterAssertion.assertRegisteredUsernameUnderProperty(usernameProperty);
        redisClusterAssertion.assertRegisteredUsernameUnderEnvironmentVariable(usernameEnvVariable);
        redisClusterAssertion.assertRegisteredPasswordUnderProperty(passwordProperty);
        redisClusterAssertion.assertRegisteredPasswordUnderEnvironmentVariable(passwordEnvVariable);
    }

    @Test
    public void shouldShutdownRedisClusterService() {
        var name = "redis-cluster-instance";
        var dockerVersion = "6.2.0";

        redisClusterService = RedisClusterDockerServiceConfigurer
                .newInstance()
                .name(name)
                .dockerImageVersion(dockerVersion)
                .done()
                .configure(ServiceConfigureContext.defaultContext());

        // when
        redisClusterService.start(ServiceStartupContext.defaultContext());

        var endpoints = redisClusterService.getEndpoints();

        redisClusterService.shutdown();

        // then
        var redisClusterAssertion = new RedisClusterAssertionHelper(redisClusterService);
        redisClusterAssertion.assertRedisClusterNotAccessible(endpoints);
    }

}
