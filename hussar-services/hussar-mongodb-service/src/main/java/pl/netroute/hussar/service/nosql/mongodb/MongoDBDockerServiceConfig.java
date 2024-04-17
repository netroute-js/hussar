package pl.netroute.hussar.service.nosql.mongodb;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.SuperBuilder;
import pl.netroute.hussar.core.service.BaseDockerServiceConfig;

import java.util.Set;

@Value
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
class MongoDBDockerServiceConfig extends BaseDockerServiceConfig {

    @NonNull
    Set<String> registerUsernameUnderProperties;

    @NonNull
    Set<String> registerUsernameUnderEnvironmentVariables;

    @NonNull
    Set<String> registerPasswordUnderProperties;

    @NonNull
    Set<String> registerPasswordUnderEnvironmentVariables;

}
