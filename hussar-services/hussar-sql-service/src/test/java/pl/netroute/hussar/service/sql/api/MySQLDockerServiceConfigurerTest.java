package pl.netroute.hussar.service.sql.api;

import pl.netroute.hussar.core.docker.api.DockerRegistry;
import pl.netroute.hussar.core.service.ServiceConfigureContext;
import pl.netroute.hussar.core.service.api.BaseDockerServiceConfigurerTest;

import java.util.function.BiConsumer;

public class MySQLDockerServiceConfigurerTest extends BaseDockerServiceConfigurerTest<MySQLDockerService, MySQLDockerServiceConfigurer> {
    private static final SQLDatabaseSchema SCHEMA = SQLDatabaseSchema.scriptLess("hussar");

    @Override
    protected ServiceTestMetadata<MySQLDockerServiceConfigurer, BiConsumer<MySQLDockerService, ServiceConfigureContext>> provideServiceConfigurerWithoutDockerRegistry() {
        var configurer = MySQLDockerServiceConfigurer
                .newInstance()
                .databaseSchema(SCHEMA)
                .done();

        var assertion = (BiConsumer<MySQLDockerService, ServiceConfigureContext>) (actualService, context) -> verifier.verifyServiceDockerRegistryConfigured(actualService, context.dockerRegistry());

        return ServiceTestMetadata
                .<MySQLDockerServiceConfigurer, BiConsumer<MySQLDockerService, ServiceConfigureContext>>newInstance()
                .configurer(configurer)
                .assertion(assertion)
                .done();
    }

    @Override
    protected ServiceTestMetadata<MySQLDockerServiceConfigurer, BiConsumer<MySQLDockerService, ServiceConfigureContext>> provideServiceConfigurerWithOverriddenDockerRegistry() {
        var dockerRegistry = new DockerRegistry("docker.netroute.pl");

        var configurer = MySQLDockerServiceConfigurer
                .newInstance()
                .databaseSchema(SCHEMA)
                .dockerRegistry(dockerRegistry)
                .done();

        var assertion = (BiConsumer<MySQLDockerService, ServiceConfigureContext>) (actualService, context) -> verifier.verifyServiceDockerRegistryConfigured(actualService, dockerRegistry);

        return ServiceTestMetadata
                .<MySQLDockerServiceConfigurer, BiConsumer<MySQLDockerService, ServiceConfigureContext>>newInstance()
                .configurer(configurer)
                .assertion(assertion)
                .done();
    }

}
