package pl.netroute.hussar.service.sql.api;

import pl.netroute.hussar.core.docker.api.DockerRegistry;
import pl.netroute.hussar.core.service.ServiceConfigureContext;
import pl.netroute.hussar.core.service.api.BaseDockerServiceConfigurerTest;

import java.util.function.BiConsumer;

public class MariaDBDockerServiceConfigurerTest extends BaseDockerServiceConfigurerTest<MariaDBDockerService, MariaDBDockerServiceConfigurer> {
    private static final SQLDatabaseSchema SCHEMA = SQLDatabaseSchema.scriptLess("hussar");

    @Override
    protected ServiceTestMetadata<MariaDBDockerServiceConfigurer, BiConsumer<MariaDBDockerService, ServiceConfigureContext>> provideServiceConfigurerWithoutDockerRegistry() {
        var configurer = MariaDBDockerServiceConfigurer
                .newInstance()
                .databaseSchema(SCHEMA)
                .done();

        var assertion = (BiConsumer<MariaDBDockerService, ServiceConfigureContext>) (actualService, context) -> verifier.verifyServiceDockerRegistryConfigured(actualService, context.dockerRegistry());

        return ServiceTestMetadata
                .<MariaDBDockerServiceConfigurer, BiConsumer<MariaDBDockerService, ServiceConfigureContext>>newInstance()
                .configurer(configurer)
                .assertion(assertion)
                .done();
    }

    @Override
    protected ServiceTestMetadata<MariaDBDockerServiceConfigurer, BiConsumer<MariaDBDockerService, ServiceConfigureContext>> provideServiceConfigurerWithOverriddenDockerRegistry() {
        var dockerRegistry = new DockerRegistry("docker.netroute.pl");

        var configurer = MariaDBDockerServiceConfigurer
                .newInstance()
                .databaseSchema(SCHEMA)
                .dockerRegistry(dockerRegistry)
                .done();

        var assertion = (BiConsumer<MariaDBDockerService, ServiceConfigureContext>) (actualService, context) -> verifier.verifyServiceDockerRegistryConfigured(actualService, dockerRegistry);

        return ServiceTestMetadata
                .<MariaDBDockerServiceConfigurer, BiConsumer<MariaDBDockerService, ServiceConfigureContext>>newInstance()
                .configurer(configurer)
                .assertion(assertion)
                .done();
    }

}
