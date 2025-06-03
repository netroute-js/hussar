package pl.netroute.hussar.service.wiremock.api;

import pl.netroute.hussar.core.docker.api.DockerRegistry;
import pl.netroute.hussar.core.service.ServiceConfigureContext;
import pl.netroute.hussar.core.service.api.BaseDockerServiceConfigurerTest;

import java.util.function.BiConsumer;

public class WiremockDockerServiceConfigurerTest extends BaseDockerServiceConfigurerTest<WiremockDockerService, WiremockDockerServiceConfigurer> {

    @Override
    protected ServiceTestMetadata<WiremockDockerServiceConfigurer, BiConsumer<WiremockDockerService, ServiceConfigureContext>> provideServiceConfigurerWithoutDockerRegistry() {
        var configurer = WiremockDockerServiceConfigurer
                .newInstance()
                .done();

        var assertion = (BiConsumer<WiremockDockerService, ServiceConfigureContext>) (actualService, context) -> verifier.verifyServiceDockerRegistryConfigured(actualService, context.dockerRegistry());

        return ServiceTestMetadata
                .<WiremockDockerServiceConfigurer, BiConsumer<WiremockDockerService, ServiceConfigureContext>>newInstance()
                .configurer(configurer)
                .assertion(assertion)
                .done();
    }

    @Override
    protected ServiceTestMetadata<WiremockDockerServiceConfigurer, BiConsumer<WiremockDockerService, ServiceConfigureContext>> provideServiceConfigurerWithOverriddenDockerRegistry() {
        var dockerRegistry = new DockerRegistry("docker.netroute.pl");

        var configurer = WiremockDockerServiceConfigurer
                .newInstance()
                .dockerRegistry(dockerRegistry)
                .done();

        var assertion = (BiConsumer<WiremockDockerService, ServiceConfigureContext>) (actualService, context) -> verifier.verifyServiceDockerRegistryConfigured(actualService, dockerRegistry);

        return ServiceTestMetadata
                .<WiremockDockerServiceConfigurer, BiConsumer<WiremockDockerService, ServiceConfigureContext>>newInstance()
                .configurer(configurer)
                .assertion(assertion)
                .done();
    }

}

