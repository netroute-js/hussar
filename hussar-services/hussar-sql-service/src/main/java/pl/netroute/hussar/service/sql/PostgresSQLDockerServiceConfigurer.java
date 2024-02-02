package pl.netroute.hussar.service.sql;

import lombok.experimental.SuperBuilder;
import pl.netroute.hussar.core.service.resolver.DockerImageResolver;
import pl.netroute.hussar.core.service.resolver.ServiceNameResolver;

@SuperBuilder(builderMethodName = "newInstance", buildMethodName = "done")
public class PostgresSQLDockerServiceConfigurer extends BaseDatabaseDockerServiceConfigurer<PostgresSQLDockerService> {
    private static final String DOCKER_IMAGE = "postgres";
    private static final String SERVICE = "postgres_service";
    private static final String JDBC_SCHEME = "jdbc:postgresql://";

    public PostgresSQLDockerService configure() {
        var config = createConfig();

        return new PostgresSQLDockerService(config);
    }

    private SQLDatabaseDockerServiceConfig createConfig() {
        var resolvedName = ServiceNameResolver.resolve(SERVICE, getName());
        var resolvedDockerImage = DockerImageResolver.resolve(DOCKER_IMAGE, getDockerImageVersion());
        var databaseSchemas = getDatabaseSchemas();
        var registerEndpointUnderProperties = getRegisterEndpointUnderProperties();
        var registerEndpointUnderEnvVariables = getRegisterEndpointUnderEnvironmentVariables();
        var registerUsernameUnderProperty = getRegisterUsernameUnderProperty();
        var registerUsernameUnderEnvVariable = getRegisterUsernameUnderEnvironmentVariable();
        var registerPasswordUnderProperty = getRegisterPasswordUnderProperty();
        var registerPasswordUnderEnvVariable = getRegisterPasswordUnderEnvironmentVariable();

        return SQLDatabaseDockerServiceConfig
                .builder()
                .name(resolvedName)
                .dockerImage(resolvedDockerImage)
                .scheme(JDBC_SCHEME)
                .databaseSchemas(databaseSchemas)
                .registerUsernameUnderProperty(registerUsernameUnderProperty)
                .registerUsernameUnderEnvironmentVariable(registerUsernameUnderEnvVariable)
                .registerPasswordUnderProperty(registerPasswordUnderProperty)
                .registerPasswordUnderEnvironmentVariable(registerPasswordUnderEnvVariable)
                .registerEndpointUnderProperties(registerEndpointUnderProperties)
                .registerEndpointUnderEnvironmentVariables(registerEndpointUnderEnvVariables)
                .build();
    }

}
