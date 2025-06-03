package pl.netroute.hussar.service.sql.api;

import pl.netroute.hussar.core.docker.api.DockerRegistry;
import pl.netroute.hussar.core.service.ServiceConfigureContext;
import pl.netroute.hussar.core.service.api.BaseDockerServiceConfigurerTest;

import java.util.function.BiConsumer;

public class PostgreSQLDockerServiceConfigurerTest extends BaseDockerServiceConfigurerTest<PostgreSQLDockerService, PostgreSQLDockerServiceConfigurer> {
    private static final SQLDatabaseSchema SCHEMA = SQLDatabaseSchema.scriptLess("hussar");

    @Override
    protected ServiceTestMetadata<PostgreSQLDockerServiceConfigurer, BiConsumer<PostgreSQLDockerService, ServiceConfigureContext>> provideServiceConfigurerWithoutDockerRegistry() {
        var configurer = PostgreSQLDockerServiceConfigurer
                .newInstance()
                .databaseSchema(SCHEMA)
                .done();

        var assertion = (BiConsumer<PostgreSQLDockerService, ServiceConfigureContext>) (actualService, context) -> verifier.verifyServiceDockerRegistryConfigured(actualService, context.dockerRegistry());

        return ServiceTestMetadata
                .<PostgreSQLDockerServiceConfigurer, BiConsumer<PostgreSQLDockerService, ServiceConfigureContext>>newInstance()
                .configurer(configurer)
                .assertion(assertion)
                .done();
    }

    @Override
    protected ServiceTestMetadata<PostgreSQLDockerServiceConfigurer, BiConsumer<PostgreSQLDockerService, ServiceConfigureContext>> provideServiceConfigurerWithOverriddenDockerRegistry() {
        var dockerRegistry = new DockerRegistry("docker.netroute.pl");

        var configurer = PostgreSQLDockerServiceConfigurer
                .newInstance()
                .databaseSchema(SCHEMA)
                .dockerRegistry(dockerRegistry)
                .done();

        var assertion = (BiConsumer<PostgreSQLDockerService, ServiceConfigureContext>) (actualService, context) -> verifier.verifyServiceDockerRegistryConfigured(actualService, dockerRegistry);

        return ServiceTestMetadata
                .<PostgreSQLDockerServiceConfigurer, BiConsumer<PostgreSQLDockerService, ServiceConfigureContext>>newInstance()
                .configurer(configurer)
                .assertion(assertion)
                .done();
    }

}
