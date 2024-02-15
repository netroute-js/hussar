package pl.netroute.hussar.service.nosql.mongodb;

import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.SuperBuilder;
import pl.netroute.hussar.core.service.BaseDockerServiceConfig;

@Value
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
class MongoDBDockerServiceConfig extends BaseDockerServiceConfig {
    String registerUsernameUnderProperty;
    String registerUsernameUnderEnvironmentVariable;

    String registerPasswordUnderProperty;
    String registerPasswordUnderEnvironmentVariable;
}
