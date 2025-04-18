package pl.netroute.hussar.service.nosql.redis;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import pl.netroute.hussar.core.helper.EndpointHelper;
import pl.netroute.hussar.core.service.ServiceConfigureContext;
import pl.netroute.hussar.core.service.ServiceStartupContext;
import pl.netroute.hussar.service.nosql.redis.api.RedisDockerService;
import pl.netroute.hussar.service.nosql.redis.api.RedisDockerServiceConfigurer;
import pl.netroute.hussar.service.nosql.redis.assertion.RedisAssertionHelper;

import java.util.Optional;

public class RedisDockerServiceIT {
    private RedisDockerService redisService;

    @AfterEach
    public void cleanup() {
        Optional
                .ofNullable(redisService)
                .ifPresent(RedisDockerService::shutdown);
    }

    @Test
    public void shouldStartRedisService() {
        // given
        redisService = RedisDockerServiceConfigurer
                .newInstance()
                .done()
                .configure(ServiceConfigureContext.defaultContext());

        // when
        redisService.start(ServiceStartupContext.defaultContext());

        // then
        var redisAssertion = new RedisAssertionHelper(redisService);
        redisAssertion.assertSingleEndpoint();
        redisAssertion.asserRedisAccessible();
        redisAssertion.assertNoEntriesRegistered();
    }

    @Test
    public void shouldStartSecuredRedisService() {
        // given
        redisService = RedisDockerServiceConfigurer
                .newInstance()
                .enablePassword(true)
                .done()
                .configure(ServiceConfigureContext.defaultContext());

        // when
        redisService.start(ServiceStartupContext.defaultContext());

        // then
        var redisAssertion = new RedisAssertionHelper(redisService);
        redisAssertion.assertSingleEndpoint();
        redisAssertion.asserRedisAccessible();
        redisAssertion.assertNoEntriesRegistered();
    }

    @Test
    public void shouldStartRedisServiceWithFullConfiguration() {
        // given
        var name = "redis-instance";
        var dockerVersion = "6.2";

        var endpointProperty = "redis.url";
        var endpointEnvVariable = "REDIS_URL";

        var usernameProperty = "redis.username";
        var usernameEnvVariable = "REDIS_USERNAME";

        var passwordProperty = "redis.password";
        var passwordEnvVariable = "REDIS_PASSWORD";

        redisService = RedisDockerServiceConfigurer
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
        redisService.start(ServiceStartupContext.defaultContext());

        // then
        var redisAssertion = new RedisAssertionHelper(redisService);
        redisAssertion.assertSingleEndpoint();
        redisAssertion.asserRedisAccessible();
        redisAssertion.assertRegisteredEndpointUnderProperty(endpointProperty);
        redisAssertion.assertRegisteredEndpointUnderEnvironmentVariable(endpointEnvVariable);
        redisAssertion.assertRegisteredUsernameUnderProperty(usernameProperty);
        redisAssertion.assertRegisteredUsernameUnderEnvironmentVariable(usernameEnvVariable);
        redisAssertion.assertRegisteredPasswordUnderProperty(passwordProperty);
        redisAssertion.assertRegisteredPasswordUnderEnvironmentVariable(passwordEnvVariable);
    }

    @Test
    public void shouldShutdownRedisService() {
        var name = "redis-instance";
        var dockerVersion = "6.2";

        redisService = RedisDockerServiceConfigurer
                .newInstance()
                .name(name)
                .dockerImageVersion(dockerVersion)
                .done()
                .configure(ServiceConfigureContext.defaultContext());

        // when
        redisService.start(ServiceStartupContext.defaultContext());

        var endpoint = EndpointHelper.getAnyEndpointOrFail(redisService);

        redisService.shutdown();

        // then
        var redisAssertion = new RedisAssertionHelper(redisService);
        redisAssertion.asserRedisNotAccessible(endpoint);
    }

}
