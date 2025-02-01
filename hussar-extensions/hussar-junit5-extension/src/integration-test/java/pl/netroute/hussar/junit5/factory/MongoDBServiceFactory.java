package pl.netroute.hussar.junit5.factory;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.netroute.hussar.service.nosql.mongodb.MongoDBDockerServiceConfigurer;

import static pl.netroute.hussar.junit5.config.ApplicationEnvironmentVariables.MONGODB_PASSWORD_ENV_VARIABLE;
import static pl.netroute.hussar.junit5.config.ApplicationEnvironmentVariables.MONGODB_URL_ENV_VARIABLE;
import static pl.netroute.hussar.junit5.config.ApplicationEnvironmentVariables.MONGODB_USERNAME_ENV_VARIABLE;
import static pl.netroute.hussar.junit5.config.ApplicationProperties.MONGODB_ALTERNATIVE_PASSWORD_PROPERTY;
import static pl.netroute.hussar.junit5.config.ApplicationProperties.MONGODB_ALTERNATIVE_URL_PROPERTY;
import static pl.netroute.hussar.junit5.config.ApplicationProperties.MONGODB_ALTERNATIVE_USERNAME_PROPERTY;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MongoDBServiceFactory {
    public static final String MONGODB_NAME = "mongodb-instance";

    public static MongoDBDockerServiceConfigurer create() {
        var dockerImageVersion = "7.0";

        return MongoDBDockerServiceConfigurer
                .newInstance()
                .name(MONGODB_NAME)
                .dockerImageVersion(dockerImageVersion)
                .registerEndpointUnderProperty(MONGODB_ALTERNATIVE_URL_PROPERTY)
                .registerEndpointUnderEnvironmentVariable(MONGODB_URL_ENV_VARIABLE)
                .registerUsernameUnderProperty(MONGODB_ALTERNATIVE_USERNAME_PROPERTY)
                .registerUsernameUnderEnvironmentVariable(MONGODB_USERNAME_ENV_VARIABLE)
                .registerPasswordUnderProperty(MONGODB_ALTERNATIVE_PASSWORD_PROPERTY)
                .registerPasswordUnderEnvironmentVariable(MONGODB_PASSWORD_ENV_VARIABLE)
                .done();
    }

}
