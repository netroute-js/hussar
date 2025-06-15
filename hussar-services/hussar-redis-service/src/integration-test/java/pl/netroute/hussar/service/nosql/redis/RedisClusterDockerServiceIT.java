package pl.netroute.hussar.service.nosql.redis;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import pl.netroute.hussar.core.api.Endpoint;
import pl.netroute.hussar.core.service.BaseServiceIT;
import pl.netroute.hussar.core.service.ServiceConfigureContext;
import pl.netroute.hussar.core.service.ServiceStartupContext;
import pl.netroute.hussar.service.nosql.redis.api.RedisClusterDockerService;
import pl.netroute.hussar.service.nosql.redis.api.RedisClusterDockerServiceConfigurer;
import pl.netroute.hussar.service.nosql.redis.assertion.RedisClusterAssertionHelper;

import java.time.Duration;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Slf4j
public class RedisClusterDockerServiceIT extends BaseServiceIT<RedisClusterDockerService> {

    @Test
    public void shouldStartSecuredRedisClusterService() {
        // given
        var context = ServiceConfigureContext.defaultContext(dockerNetwork, networkOperator.getNetworkConfigurer());

        service = RedisClusterDockerServiceConfigurer
                .newInstance()
                .enablePassword(true)
                .done()
                .configure(context);

        // when
        service.start(ServiceStartupContext.defaultContext());

        // then
        var redisClusterAssertion = new RedisClusterAssertionHelper(service);
        redisClusterAssertion.assertMultipleEndpoints();
        redisClusterAssertion.assertRedisClusterAccessible();
        redisClusterAssertion.assertNoEntriesRegistered();
    }

    @Override
    protected ServiceTestMetadata<RedisClusterDockerService, Consumer<RedisClusterDockerService>> provideMinimallyConfiguredServiceTestMetadata(ServiceConfigureContext context) {
        var service = RedisClusterDockerServiceTestFactory.createMinimallyConfigured(context);

        var assertion = (Consumer<RedisClusterDockerService>) actualService -> {
            var redisClusterAssertion = new RedisClusterAssertionHelper(actualService);
            redisClusterAssertion.assertMultipleEndpoints();
            redisClusterAssertion.assertRedisClusterAccessible();
            redisClusterAssertion.assertNoEntriesRegistered();
        };

        return ServiceTestMetadata
                .<RedisClusterDockerService, Consumer<RedisClusterDockerService>>newInstance()
                .service(service)
                .assertion(assertion)
                .done();
    }

    @Override
    protected ServiceTestMetadata<RedisClusterDockerService, Consumer<RedisClusterDockerService>> provideFullyConfiguredServiceTestMetadata(ServiceConfigureContext context) {
        var name = "redis-cluster-instance";
        var dockerVersion = "6.2.0";

        var startupTimeout = Duration.ofSeconds(100L);

        var endpointProperty = "redis.cluster.url";
        var endpointEnvVariable = "REDIS_CLUSTER_URL";

        var usernameProperty = "redis.cluster.username";
        var usernameEnvVariable = "REDIS_CLUSTER_USERNAME";

        var passwordProperty = "redis.cluster.password";
        var passwordEnvVariable = "REDIS_CLUSTER_PASSWORD";

        var service = RedisClusterDockerServiceConfigurer
                .newInstance()
                .name(name)
                .dockerImageVersion(dockerVersion)
                .startupTimeout(startupTimeout)
                .enablePassword(true)
                .registerEndpointUnderProperty(endpointProperty)
                .registerEndpointUnderEnvironmentVariable(endpointEnvVariable)
                .registerUsernameUnderProperty(usernameProperty)
                .registerUsernameUnderEnvironmentVariable(usernameEnvVariable)
                .registerPasswordUnderProperty(passwordProperty)
                .registerPasswordUnderEnvironmentVariable(passwordEnvVariable)
                .done()
                .configure(context);

        var assertion = (Consumer<RedisClusterDockerService>) actualService -> {
            var redisClusterAssertion = new RedisClusterAssertionHelper(actualService);
            redisClusterAssertion.assertMultipleEndpoints();
            redisClusterAssertion.assertRedisClusterAccessible();
            redisClusterAssertion.assertRegisteredEndpointsUnderProperty(endpointProperty);
            redisClusterAssertion.assertRegisteredEndpointsUnderEnvironmentVariable(endpointEnvVariable);
            redisClusterAssertion.assertRegisteredUsernameUnderProperty(usernameProperty);
            redisClusterAssertion.assertRegisteredUsernameUnderEnvironmentVariable(usernameEnvVariable);
            redisClusterAssertion.assertRegisteredPasswordUnderProperty(passwordProperty);
            redisClusterAssertion.assertRegisteredPasswordUnderEnvironmentVariable(passwordEnvVariable);
        };

        return ServiceTestMetadata
                .<RedisClusterDockerService, Consumer<RedisClusterDockerService>>newInstance()
                .service(service)
                .assertion(assertion)
                .done();
    }

    @Override
    protected ServiceTestMetadata<RedisClusterDockerService, BiConsumer<RedisClusterDockerService, List<Endpoint>>> provideShutdownServiceTestMetadata(ServiceConfigureContext context) {
        var service = RedisClusterDockerServiceTestFactory.createMinimallyConfigured(context);

        var assertion = (BiConsumer<RedisClusterDockerService, List<Endpoint>>) (actualService, endpoints) -> {
            var redisClusterAssertion = new RedisClusterAssertionHelper(actualService);
            redisClusterAssertion.assertRedisClusterNotAccessible(endpoints);
        };

        return ServiceTestMetadata
                .<RedisClusterDockerService, BiConsumer<RedisClusterDockerService, List<Endpoint>>>newInstance()
                .service(service)
                .assertion(assertion)
                .done();
    }

}
