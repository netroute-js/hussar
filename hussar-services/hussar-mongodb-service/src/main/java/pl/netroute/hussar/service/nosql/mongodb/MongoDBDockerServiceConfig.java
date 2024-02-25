package pl.netroute.hussar.service.nosql.mongodb;

import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.SuperBuilder;
import pl.netroute.hussar.core.service.BaseDockerServiceConfig;

import java.util.Set;

@Value
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
class MongoDBDockerServiceConfig extends BaseDockerServiceConfig {
    Set<String> registerUsernameUnderProperties;
    Set<String> registerUsernameUnderEnvironmentVariables;

    Set<String> registerPasswordUnderProperties;
    Set<String> registerPasswordUnderEnvironmentVariables;
}
