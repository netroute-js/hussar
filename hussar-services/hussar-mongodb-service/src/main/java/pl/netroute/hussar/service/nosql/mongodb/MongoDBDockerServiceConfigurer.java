package pl.netroute.hussar.service.nosql.mongodb;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import pl.netroute.hussar.core.service.BaseDockerServiceConfigurer;
import pl.netroute.hussar.core.service.resolver.DockerImageResolver;
import pl.netroute.hussar.core.service.resolver.ServiceNameResolver;

@Getter
@SuperBuilder(builderMethodName = "newInstance", buildMethodName = "done")
public class MongoDBDockerServiceConfigurer extends BaseDockerServiceConfigurer<MongoDBDockerService> {
    private static final String DOCKER_IMAGE = "mongo";
    private static final String SERVICE = "mongodb_service";
    private static final String MONGODB_SCHEME = "mongodb://";

    private final String registerUsernameUnderProperty;
    private final String registerUsernameUnderEnvironmentVariable;

    private final String registerPasswordUnderProperty;
    private final String registerPasswordUnderEnvironmentVariable;

    public MongoDBDockerService configure() {
        var config = createConfig();

        return new MongoDBDockerService(config);
    }

    private MongoDBDockerServiceConfig createConfig() {
        var resolvedName = ServiceNameResolver.resolve(SERVICE, getName());
        var resolvedDockerImage = DockerImageResolver.resolve(DOCKER_IMAGE, getDockerImageVersion());

        var registerEndpointUnderProperties = getRegisterEndpointUnderProperties();
        var registerEndpointUnderEnvVariables = getRegisterEndpointUnderEnvironmentVariables();
        var registerUsernameUnderProperty = getRegisterUsernameUnderProperty();
        var registerUsernameUnderEnvVariable = getRegisterUsernameUnderEnvironmentVariable();
        var registerPasswordUnderProperty = getRegisterPasswordUnderProperty();
        var registerPasswordUnderEnvVariable = getRegisterPasswordUnderEnvironmentVariable();

        return MongoDBDockerServiceConfig
                .builder()
                .name(resolvedName)
                .dockerImage(resolvedDockerImage)
                .scheme(MONGODB_SCHEME)
                .registerUsernameUnderProperty(registerUsernameUnderProperty)
                .registerUsernameUnderEnvironmentVariable(registerUsernameUnderEnvVariable)
                .registerPasswordUnderProperty(registerPasswordUnderProperty)
                .registerPasswordUnderEnvironmentVariable(registerPasswordUnderEnvVariable)
                .registerEndpointUnderProperties(registerEndpointUnderProperties)
                .registerEndpointUnderEnvironmentVariables(registerEndpointUnderEnvVariables)
                .build();
    }
}
