package pl.netroute.hussar.service.nosql.redis.api;

import pl.netroute.hussar.core.docker.api.DockerRegistry;
import pl.netroute.hussar.core.service.ServiceConfigureContext;
import pl.netroute.hussar.core.service.api.BaseDockerServiceConfigurerTest;

import java.util.function.BiConsumer;

public class RedisDockerServiceConfigurerTest extends BaseDockerServiceConfigurerTest<RedisDockerService, RedisDockerServiceConfigurer> {

    @Override
    protected ServiceTestMetadata<RedisDockerServiceConfigurer, BiConsumer<RedisDockerService, ServiceConfigureContext>> provideServiceConfigurerWithoutDockerRegistry() {
        var configurer = RedisDockerServiceConfigurer
                .newInstance()
                .done();

        var assertion = (BiConsumer<RedisDockerService, ServiceConfigureContext>) (actualService, context) -> verifier.verifyServiceDockerRegistryConfigured(actualService, context.dockerRegistry());

        return ServiceTestMetadata
                .<RedisDockerServiceConfigurer, BiConsumer<RedisDockerService, ServiceConfigureContext>>newInstance()
                .configurer(configurer)
                .assertion(assertion)
                .done();
    }

    @Override
    protected ServiceTestMetadata<RedisDockerServiceConfigurer, BiConsumer<RedisDockerService, ServiceConfigureContext>> provideServiceConfigurerWithOverriddenDockerRegistry() {
        var dockerRegistry = new DockerRegistry("docker.netroute.pl");

        var configurer = RedisDockerServiceConfigurer
                .newInstance()
                .dockerRegistry(dockerRegistry)
                .done();

        var assertion = (BiConsumer<RedisDockerService, ServiceConfigureContext>) (actualService, context) -> verifier.verifyServiceDockerRegistryConfigured(actualService, dockerRegistry);

        return ServiceTestMetadata
                .<RedisDockerServiceConfigurer, BiConsumer<RedisDockerService, ServiceConfigureContext>>newInstance()
                .configurer(configurer)
                .assertion(assertion)
                .done();
    }

}

