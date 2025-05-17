package pl.netroute.hussar.service.nosql.redis;

import pl.netroute.hussar.core.service.BaseServiceNetworkIT;
import pl.netroute.hussar.core.service.ServiceConfigureContext;
import pl.netroute.hussar.service.nosql.redis.api.RedisDockerService;
import pl.netroute.hussar.service.nosql.redis.assertion.RedisAssertionHelper;

import java.util.function.Consumer;

class RedisDockerServiceNetworkIT extends BaseServiceNetworkIT<RedisDockerService> {

    @Override
    protected ServiceTestMetadata<RedisDockerService, Consumer<RedisDockerService>> provideEnableNetworkTestMetadata(ServiceConfigureContext context) {
        var service = RedisDockerServiceTestFactory.createMinimallyConfigured(context);

        var assertion = (Consumer<RedisDockerService>) actualService -> {
            var redisAssertion = new RedisAssertionHelper(actualService);
            redisAssertion.asserRedisAccessible();
        };

        return ServiceTestMetadata
                .<RedisDockerService, Consumer<RedisDockerService>>newInstance()
                .service(service)
                .assertion(assertion)
                .done();
    }

    @Override
    protected ServiceTestMetadata<RedisDockerService, Consumer<RedisDockerService>> provideDisableNetworkTestMetadata(ServiceConfigureContext context) {
        var service = RedisDockerServiceTestFactory.createMinimallyConfigured(context);

        var assertion = (Consumer<RedisDockerService>) actualService -> {
            var redisAssertion = new RedisAssertionHelper(actualService);
            redisAssertion.assertRedisNotAccessible();
        };

        return ServiceTestMetadata
                .<RedisDockerService, Consumer<RedisDockerService>>newInstance()
                .service(service)
                .assertion(assertion)
                .done();
    }

    @Override
    protected ServiceTestMetadata<RedisDockerService, Consumer<RedisDockerService>> provideIntroduceNetworkLatencyTestMetadata(ServiceConfigureContext context) {
        var service = RedisDockerServiceTestFactory.createMinimallyConfigured(context);

        var assertion = (Consumer<RedisDockerService>) actualService -> {
            var redisAssertion = new RedisAssertionHelper(actualService);
            redisAssertion.asserRedisAccessible();
        };

        return ServiceTestMetadata
                .<RedisDockerService, Consumer<RedisDockerService>>newInstance()
                .service(service)
                .assertion(assertion)
                .done();
    }

    @Override
    protected ServiceTestMetadata<RedisDockerService, Consumer<RedisDockerService>> provideResetNetworkTestMetadata(ServiceConfigureContext context) {
        var service = RedisDockerServiceTestFactory.createMinimallyConfigured(context);

        var assertion = (Consumer<RedisDockerService>) actualService -> {
            var redisAssertion = new RedisAssertionHelper(actualService);
            redisAssertion.asserRedisAccessible();
        };

        return ServiceTestMetadata
                .<RedisDockerService, Consumer<RedisDockerService>>newInstance()
                .service(service)
                .assertion(assertion)
                .done();
    }

}
