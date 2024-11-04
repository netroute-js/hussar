package pl.netroute.hussar.service.sql;

import lombok.experimental.SuperBuilder;
import pl.netroute.hussar.core.api.configuration.DefaultConfigurationRegistry;
import pl.netroute.hussar.core.service.container.GenericContainerFactory;
import pl.netroute.hussar.core.service.registerer.EndpointRegisterer;
import pl.netroute.hussar.core.service.resolver.DockerImageResolver;
import pl.netroute.hussar.core.service.resolver.ServiceNameResolver;
import pl.netroute.hussar.service.sql.schema.DatabaseSchemaInitializer;

/**
 * Hussar {@link MySQLDockerService} configurer. This is the only way to create {@link MySQLDockerService}.
 */
@SuperBuilder(builderMethodName = "newInstance", buildMethodName = "done")
public class MySQLDockerServiceConfigurer extends BaseDatabaseDockerServiceConfigurer<MySQLDockerService> {
    private static final String DOCKER_IMAGE = "mysql";
    private static final String SERVICE = "mysql_service";
    private static final String JDBC_SCHEME = "jdbc:mysql://";

    public MySQLDockerService configure() {
        var config = createConfig();
        var container = GenericContainerFactory.create(config);
        var configurationRegistry = new DefaultConfigurationRegistry();
        var endpointRegisterer = new EndpointRegisterer(configurationRegistry);
        var credentialsRegisterer = new DatabaseCredentialsRegisterer(configurationRegistry);
        var schemaInitializer = new DatabaseSchemaInitializer();

        return new MySQLDockerService(
                container,
                config,
                configurationRegistry,
                endpointRegisterer,
                credentialsRegisterer,
                schemaInitializer
        );
    }

    private SQLDatabaseDockerServiceConfig createConfig() {
        var resolvedName = ServiceNameResolver.resolve(SERVICE, name);
        var resolvedDockerImage = DockerImageResolver.resolve(dockerRegistryUrl, DOCKER_IMAGE, dockerImageVersion);

        return SQLDatabaseDockerServiceConfig
                .builder()
                .name(resolvedName)
                .dockerImage(resolvedDockerImage)
                .scheme(JDBC_SCHEME)
                .databaseSchemas(databaseSchemas)
                .registerUsernameUnderProperties(registerUsernameUnderProperties)
                .registerUsernameUnderEnvironmentVariables(registerUsernameUnderEnvironmentVariables)
                .registerPasswordUnderProperties(registerPasswordUnderProperties)
                .registerPasswordUnderEnvironmentVariables(registerPasswordUnderEnvironmentVariables)
                .registerEndpointUnderProperties(registerEndpointUnderProperties)
                .registerEndpointUnderEnvironmentVariables(registerEndpointUnderEnvironmentVariables)
                .build();
    }

}
