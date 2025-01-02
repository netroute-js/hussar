package pl.netroute.hussar.service.sql;

import lombok.experimental.SuperBuilder;
import org.testcontainers.utility.DockerImageName;
import pl.netroute.hussar.core.configuration.api.DefaultConfigurationRegistry;
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
        var dockerImage = DockerImageResolver.resolve(dockerRegistryUrl, DOCKER_IMAGE, dockerImageVersion);
        var config = createConfig(dockerImage);
        var container = GenericContainerFactory.create(dockerImage);
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

    private SQLDatabaseDockerServiceConfig createConfig(DockerImageName dockerImage) {
        var resolvedName = ServiceNameResolver.resolve(SERVICE, name);

        return SQLDatabaseDockerServiceConfig
                .builder()
                .name(resolvedName)
                .dockerImage(dockerImage.asCanonicalNameString())
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
