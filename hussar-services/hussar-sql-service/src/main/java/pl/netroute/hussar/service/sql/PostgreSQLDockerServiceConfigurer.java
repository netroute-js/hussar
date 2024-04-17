package pl.netroute.hussar.service.sql;

import lombok.experimental.SuperBuilder;
import pl.netroute.hussar.core.api.MapConfigurationRegistry;
import pl.netroute.hussar.core.service.container.GenericContainerFactory;
import pl.netroute.hussar.core.service.registerer.EndpointRegisterer;
import pl.netroute.hussar.core.service.resolver.DockerImageResolver;
import pl.netroute.hussar.core.service.resolver.ServiceNameResolver;
import pl.netroute.hussar.service.sql.registerer.DatabaseCredentialsRegisterer;
import pl.netroute.hussar.service.sql.schema.DatabaseSchemaInitializer;

@SuperBuilder(builderMethodName = "newInstance", buildMethodName = "done")
public class PostgreSQLDockerServiceConfigurer extends BaseDatabaseDockerServiceConfigurer<PostgreSQLDockerService> {
    private static final String DOCKER_IMAGE = "postgres";
    private static final String SERVICE = "postgres_service";
    private static final String JDBC_SCHEME = "jdbc:postgresql://";

    public PostgreSQLDockerService configure() {
        var config = createConfig();
        var container = GenericContainerFactory.create(config);
        var configurationRegistry = new MapConfigurationRegistry();
        var endpointRegisterer = new EndpointRegisterer(configurationRegistry);
        var credentialsRegisterer = new DatabaseCredentialsRegisterer(configurationRegistry);
        var schemeInitializer = new DatabaseSchemaInitializer();

        return new PostgreSQLDockerService(
                container,
                config,
                configurationRegistry,
                endpointRegisterer,
                credentialsRegisterer,
                schemeInitializer
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
