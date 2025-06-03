package pl.netroute.hussar.service.nosql.mongodb.api;

import pl.netroute.hussar.core.docker.api.DockerRegistry;
import pl.netroute.hussar.core.service.ServiceConfigureContext;
import pl.netroute.hussar.core.service.api.BaseDockerServiceConfigurerTest;

import java.util.function.BiConsumer;

public class MongoDBDockerServiceConfigurerTest extends BaseDockerServiceConfigurerTest<MongoDBDockerService, MongoDBDockerServiceConfigurer> {

    @Override
    protected ServiceTestMetadata<MongoDBDockerServiceConfigurer, BiConsumer<MongoDBDockerService, ServiceConfigureContext>> provideServiceConfigurerWithoutDockerRegistry() {
        var configurer = MongoDBDockerServiceConfigurer
                .newInstance()
                .done();

        var assertion = (BiConsumer<MongoDBDockerService, ServiceConfigureContext>) (actualService, context) -> verifier.verifyServiceDockerRegistryConfigured(actualService, context.dockerRegistry());

        return ServiceTestMetadata
                .<MongoDBDockerServiceConfigurer, BiConsumer<MongoDBDockerService, ServiceConfigureContext>>newInstance()
                .configurer(configurer)
                .assertion(assertion)
                .done();
    }

    @Override
    protected ServiceTestMetadata<MongoDBDockerServiceConfigurer, BiConsumer<MongoDBDockerService, ServiceConfigureContext>> provideServiceConfigurerWithOverriddenDockerRegistry() {
        var dockerRegistry = new DockerRegistry("docker.netroute.pl");

        var configurer = MongoDBDockerServiceConfigurer
                .newInstance()
                .dockerRegistry(dockerRegistry)
                .done();

        var assertion = (BiConsumer<MongoDBDockerService, ServiceConfigureContext>) (actualService, context) -> verifier.verifyServiceDockerRegistryConfigured(actualService, dockerRegistry);

        return ServiceTestMetadata
                .<MongoDBDockerServiceConfigurer, BiConsumer<MongoDBDockerService, ServiceConfigureContext>>newInstance()
                .configurer(configurer)
                .assertion(assertion)
                .done();
    }

}

