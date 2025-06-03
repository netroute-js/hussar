package pl.netroute.hussar.service.rabbitmq.api;

import pl.netroute.hussar.core.docker.api.DockerRegistry;
import pl.netroute.hussar.core.service.ServiceConfigureContext;
import pl.netroute.hussar.core.service.api.BaseDockerServiceConfigurerTest;

import java.util.function.BiConsumer;

public class RabbitMQDockerServiceConfigurerTest extends BaseDockerServiceConfigurerTest<RabbitMQDockerService, RabbitMQDockerServiceConfigurer> {

    @Override
    protected ServiceTestMetadata<RabbitMQDockerServiceConfigurer, BiConsumer<RabbitMQDockerService, ServiceConfigureContext>> provideServiceConfigurerWithoutDockerRegistry() {
        var configurer = RabbitMQDockerServiceConfigurer
                .newInstance()
                .done();

        var assertion = (BiConsumer<RabbitMQDockerService, ServiceConfigureContext>) (actualService, context) -> verifier.verifyServiceDockerRegistryConfigured(actualService, context.dockerRegistry());

        return ServiceTestMetadata
                .<RabbitMQDockerServiceConfigurer, BiConsumer<RabbitMQDockerService, ServiceConfigureContext>>newInstance()
                .configurer(configurer)
                .assertion(assertion)
                .done();
    }

    @Override
    protected ServiceTestMetadata<RabbitMQDockerServiceConfigurer, BiConsumer<RabbitMQDockerService, ServiceConfigureContext>> provideServiceConfigurerWithOverriddenDockerRegistry() {
        var dockerRegistry = new DockerRegistry("docker.netroute.pl");

        var configurer = RabbitMQDockerServiceConfigurer
                .newInstance()
                .dockerRegistry(dockerRegistry)
                .done();

        var assertion = (BiConsumer<RabbitMQDockerService, ServiceConfigureContext>) (actualService, context) -> verifier.verifyServiceDockerRegistryConfigured(actualService, dockerRegistry);

        return ServiceTestMetadata
                .<RabbitMQDockerServiceConfigurer, BiConsumer<RabbitMQDockerService, ServiceConfigureContext>>newInstance()
                .configurer(configurer)
                .assertion(assertion)
                .done();
    }

}

