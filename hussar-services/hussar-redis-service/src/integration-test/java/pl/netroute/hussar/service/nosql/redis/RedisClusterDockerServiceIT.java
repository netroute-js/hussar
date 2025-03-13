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

}
