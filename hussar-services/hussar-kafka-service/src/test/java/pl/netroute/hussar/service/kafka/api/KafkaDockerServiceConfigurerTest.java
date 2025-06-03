package pl.netroute.hussar.service.kafka.api;

import pl.netroute.hussar.core.docker.api.DockerRegistry;
import pl.netroute.hussar.core.service.ServiceConfigureContext;
import pl.netroute.hussar.core.service.api.BaseDockerServiceConfigurerTest;

import java.util.function.BiConsumer;

public class KafkaDockerServiceConfigurerTest extends BaseDockerServiceConfigurerTest<KafkaDockerService, KafkaDockerServiceConfigurer> {

    @Override
    protected ServiceTestMetadata<KafkaDockerServiceConfigurer, BiConsumer<KafkaDockerService, ServiceConfigureContext>> provideServiceConfigurerWithoutDockerRegistry() {
        var configurer = KafkaDockerServiceConfigurer
                .newInstance()
                .done();

        var assertion = (BiConsumer<KafkaDockerService, ServiceConfigureContext>) (actualService, context) -> verifier.verifyServiceDockerRegistryConfigured(actualService, context.dockerRegistry());

        return ServiceTestMetadata
                .<KafkaDockerServiceConfigurer, BiConsumer<KafkaDockerService, ServiceConfigureContext>>newInstance()
                .configurer(configurer)
                .assertion(assertion)
                .done();
    }

    @Override
    protected ServiceTestMetadata<KafkaDockerServiceConfigurer, BiConsumer<KafkaDockerService, ServiceConfigureContext>> provideServiceConfigurerWithOverriddenDockerRegistry() {
        var dockerRegistry = new DockerRegistry("docker.netroute.pl");

        var configurer = KafkaDockerServiceConfigurer
                .newInstance()
                .dockerRegistry(dockerRegistry)
                .done();

        var assertion = (BiConsumer<KafkaDockerService, ServiceConfigureContext>) (actualService, context) -> verifier.verifyServiceDockerRegistryConfigured(actualService, dockerRegistry);

        return ServiceTestMetadata
                .<KafkaDockerServiceConfigurer, BiConsumer<KafkaDockerService, ServiceConfigureContext>>newInstance()
                .configurer(configurer)
                .assertion(assertion)
                .done();
    }

}

