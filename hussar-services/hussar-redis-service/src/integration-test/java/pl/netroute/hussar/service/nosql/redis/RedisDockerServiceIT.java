package pl.netroute.hussar.service.nosql.redis;

import org.junit.jupiter.api.Test;
import pl.netroute.hussar.core.api.Endpoint;
import pl.netroute.hussar.core.service.BaseServiceIT;
import pl.netroute.hussar.core.service.ServiceConfigureContext;
import pl.netroute.hussar.core.service.ServiceStartupContext;
import pl.netroute.hussar.service.nosql.redis.api.RedisDockerService;
import pl.netroute.hussar.service.nosql.redis.api.RedisDockerServiceConfigurer;
import pl.netroute.hussar.service.nosql.redis.assertion.RedisAssertionHelper;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class RedisDockerServiceIT extends BaseServiceIT<RedisDockerService> {

    @Test
    public void shouldStartSecuredService() {
        // given
        var context = ServiceConfigureContext.defaultContext(networkOperator.getNetworkConfigurer());

        service = RedisDockerServiceConfigurer
                .newInstance()
                .enablePassword(true)
                .done()
                .configure(context);

        // when
        service.start(ServiceStartupContext.defaultContext());

        // then
        var redisAssertion = new RedisAssertionHelper(service);
        redisAssertion.assertSingleEndpoint();
        redisAssertion.asserRedisAccessible();
        redisAssertion.assertNoEntriesRegistered();
    }

    @Override
    protected ServiceTestMetadata<RedisDockerService, Consumer<RedisDockerService>> provideMinimallyConfiguredServiceTestMetadata(ServiceConfigureContext context) {
        var service = RedisDockerServiceTestFactory.createMinimallyConfigured(context);

        var assertion = (Consumer<RedisDockerService>) actualService -> {
            var redisAssertion = new RedisAssertionHelper(actualService);
            redisAssertion.assertSingleEndpoint();
            redisAssertion.asserRedisAccessible();
            redisAssertion.assertNoEntriesRegistered();
        };

        return ServiceTestMetadata
                .<RedisDockerService, Consumer<RedisDockerService>>newInstance()
                .service(service)
                .assertion(assertion)
                .done();
    }

    @Override
    protected ServiceTestMetadata<RedisDockerService, Consumer<RedisDockerService>> provideFullyConfiguredServiceTestMetadata(ServiceConfigureContext context) {
        var name = "redis-instance";
        var dockerVersion = "6.2";

        var endpointProperty = "redis.url";
        var endpointEnvVariable = "REDIS_URL";

        var usernameProperty = "redis.username";
        var usernameEnvVariable = "REDIS_USERNAME";

        var passwordProperty = "redis.password";
        var passwordEnvVariable = "REDIS_PASSWORD";

        var service = RedisDockerServiceConfigurer
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
                .configure(context);

        var assertion = (Consumer<RedisDockerService>) actualService -> {
            var redisAssertion = new RedisAssertionHelper(actualService);
            redisAssertion.assertSingleEndpoint();
            redisAssertion.asserRedisAccessible();
            redisAssertion.assertRegisteredEndpointUnderProperty(endpointProperty);
            redisAssertion.assertRegisteredEndpointUnderEnvironmentVariable(endpointEnvVariable);
            redisAssertion.assertRegisteredUsernameUnderProperty(usernameProperty);
            redisAssertion.assertRegisteredUsernameUnderEnvironmentVariable(usernameEnvVariable);
            redisAssertion.assertRegisteredPasswordUnderProperty(passwordProperty);
            redisAssertion.assertRegisteredPasswordUnderEnvironmentVariable(passwordEnvVariable);
        };

        return ServiceTestMetadata
                .<RedisDockerService, Consumer<RedisDockerService>>newInstance()
                .service(service)
                .assertion(assertion)
                .done();
    }

    @Override
    protected ServiceTestMetadata<RedisDockerService, BiConsumer<RedisDockerService, List<Endpoint>>> provideShutdownServiceTestMetadata(ServiceConfigureContext context) {
        var service = RedisDockerServiceTestFactory.createMinimallyConfigured(context);

        var assertion = (BiConsumer<RedisDockerService, List<Endpoint>>) (actualService, endpoints) -> {
            var endpoint = endpoints.getFirst();

            var redisAssertion = new RedisAssertionHelper(actualService);
            redisAssertion.assertRedisNotAccessible(endpoint);
        };

        return ServiceTestMetadata
                .<RedisDockerService, BiConsumer<RedisDockerService, List<Endpoint>>>newInstance()
                .service(service)
                .assertion(assertion)
                .done();
    }

}
