package pl.netroute.hussar.service.sql;

import lombok.experimental.SuperBuilder;
import pl.netroute.hussar.core.api.configuration.DefaultConfigurationRegistry;
import pl.netroute.hussar.core.service.container.GenericContainerFactory;
import pl.netroute.hussar.core.service.registerer.EndpointRegisterer;
import pl.netroute.hussar.core.service.resolver.DockerImageResolver;
import pl.netroute.hussar.core.service.resolver.ServiceNameResolver;
import pl.netroute.hussar.service.sql.schema.DatabaseSchemaInitializer;

/**
 * Hussar {@link MariaDBDockerService} configurer. This is the only way to create {@link MariaDBDockerService}.
 */
@SuperBuilder(builderMethodName = "newInstance", buildMethodName = "done")
public class MariaDBDockerServiceConfigurer extends BaseDatabaseDockerServiceConfigurer<MariaDBDockerService> {
    private static final String DOCKER_IMAGE = "mariadb";
    private static final String SERVICE = "mariadb_service";
    private static final String JDBC_SCHEME = "jdbc:mariadb://";

    public MariaDBDockerService configure() {
        var config = createConfig();
        var container = GenericContainerFactory.create(config);
        var configurationRegistry = new DefaultConfigurationRegistry();
        var endpointRegisterer = new EndpointRegisterer(configurationRegistry);
        var credentialsRegisterer = new DatabaseCredentialsRegisterer(configurationRegistry);
        var schemaInitializer = new DatabaseSchemaInitializer();

        return new MariaDBDockerService(
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
        var resolvedDockerImage = DockerImageResolver.resolve(DOCKER_IMAGE, dockerImageVersion);

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
