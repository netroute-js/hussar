package pl.netroute.hussar.service.nosql.redis;

import pl.netroute.hussar.core.service.BaseServiceNetworkIT;
import pl.netroute.hussar.core.service.ServiceConfigureContext;
import pl.netroute.hussar.service.nosql.redis.api.RedisClusterDockerService;
import pl.netroute.hussar.service.nosql.redis.assertion.RedisClusterAssertionHelper;

import java.util.function.Consumer;

class RedisClusterDockerServiceNetworkIT extends BaseServiceNetworkIT<RedisClusterDockerService> {

    @Override
    protected ServiceTestMetadata<RedisClusterDockerService, Consumer<RedisClusterDockerService>> provideEnableNetworkTestMetadata(ServiceConfigureContext context) {
        var service = RedisClusterDockerServiceTestFactory.createMinimallyConfigured(context);

        var assertion = (Consumer<RedisClusterDockerService>) actualService -> {
            var redisAssertion = new RedisClusterAssertionHelper(actualService);
            redisAssertion.assertRedisClusterAccessible();
        };

        return ServiceTestMetadata
                .<RedisClusterDockerService, Consumer<RedisClusterDockerService>>newInstance()
                .service(service)
                .assertion(assertion)
                .done();
    }

    @Override
    protected ServiceTestMetadata<RedisClusterDockerService, Consumer<RedisClusterDockerService>> provideDisableNetworkTestMetadata(ServiceConfigureContext context) {
        var service = RedisClusterDockerServiceTestFactory.createMinimallyConfigured(context);

        var assertion = (Consumer<RedisClusterDockerService>) actualService -> {
            var redisAssertion = new RedisClusterAssertionHelper(actualService);
            redisAssertion.assertRedisClusterNotAccessible();
        };

        return ServiceTestMetadata
                .<RedisClusterDockerService, Consumer<RedisClusterDockerService>>newInstance()
                .service(service)
                .assertion(assertion)
                .done();
    }

    @Override
    protected ServiceTestMetadata<RedisClusterDockerService, Consumer<RedisClusterDockerService>> provideIntroduceNetworkLatencyTestMetadata(ServiceConfigureContext context) {
        var service = RedisClusterDockerServiceTestFactory.createMinimallyConfigured(context);

        var assertion = (Consumer<RedisClusterDockerService>) actualService -> {
            var redisAssertion = new RedisClusterAssertionHelper(actualService);
            redisAssertion.assertRedisClusterAccessible();
        };

        return ServiceTestMetadata
                .<RedisClusterDockerService, Consumer<RedisClusterDockerService>>newInstance()
                .service(service)
                .assertion(assertion)
                .done();
    }

    @Override
    protected ServiceTestMetadata<RedisClusterDockerService, Consumer<RedisClusterDockerService>> provideResetNetworkTestMetadata(ServiceConfigureContext context) {
        var service = RedisClusterDockerServiceTestFactory.createMinimallyConfigured(context);

        var assertion = (Consumer<RedisClusterDockerService>) actualService -> {
            var redisAssertion = new RedisClusterAssertionHelper(actualService);
            redisAssertion.assertRedisClusterAccessible();
        };

        return ServiceTestMetadata
                .<RedisClusterDockerService, Consumer<RedisClusterDockerService>>newInstance()
                .service(service)
                .assertion(assertion)
                .done();
    }

}
