package pl.netroute.hussar.service.nosql.redis.api;

import pl.netroute.hussar.core.docker.api.DockerRegistry;
import pl.netroute.hussar.core.service.ServiceConfigureContext;
import pl.netroute.hussar.core.service.api.BaseDockerServiceConfigurerTest;

import java.util.function.BiConsumer;

public class RedisClusterDockerServiceConfigurerTest extends BaseDockerServiceConfigurerTest<RedisClusterDockerService, RedisClusterDockerServiceConfigurer> {

    @Override
    protected ServiceTestMetadata<RedisClusterDockerServiceConfigurer, BiConsumer<RedisClusterDockerService, ServiceConfigureContext>> provideServiceConfigurerWithoutDockerRegistry() {
        var configurer = RedisClusterDockerServiceConfigurer
                .newInstance()
                .done();

        var assertion = (BiConsumer<RedisClusterDockerService, ServiceConfigureContext>) (actualService, context) -> verifier.verifyServiceDockerRegistryConfigured(actualService, context.dockerRegistry());

        return ServiceTestMetadata
                .<RedisClusterDockerServiceConfigurer, BiConsumer<RedisClusterDockerService, ServiceConfigureContext>>newInstance()
                .configurer(configurer)
                .assertion(assertion)
                .done();
    }

    @Override
    protected ServiceTestMetadata<RedisClusterDockerServiceConfigurer, BiConsumer<RedisClusterDockerService, ServiceConfigureContext>> provideServiceConfigurerWithOverriddenDockerRegistry() {
        var dockerRegistry = new DockerRegistry("docker.netroute.pl");

        var configurer = RedisClusterDockerServiceConfigurer
                .newInstance()
                .dockerRegistry(dockerRegistry)
                .done();

        var assertion = (BiConsumer<RedisClusterDockerService, ServiceConfigureContext>) (actualService, context) -> verifier.verifyServiceDockerRegistryConfigured(actualService, dockerRegistry);

        return ServiceTestMetadata
                .<RedisClusterDockerServiceConfigurer, BiConsumer<RedisClusterDockerService, ServiceConfigureContext>>newInstance()
                .configurer(configurer)
                .assertion(assertion)
                .done();
    }

}

