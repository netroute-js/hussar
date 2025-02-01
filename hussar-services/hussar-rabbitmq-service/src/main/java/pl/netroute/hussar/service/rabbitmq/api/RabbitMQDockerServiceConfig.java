package pl.netroute.hussar.service.rabbitmq.api;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.SuperBuilder;
import pl.netroute.hussar.core.api.InternalUseOnly;
import pl.netroute.hussar.core.service.api.BaseDockerServiceConfig;

import java.util.Set;

@Value
@SuperBuilder
@InternalUseOnly
@EqualsAndHashCode(callSuper = true)
class RabbitMQDockerServiceConfig extends BaseDockerServiceConfig {

    @NonNull
    Set<RabbitMQQueue> queues;

    @NonNull
    Set<String> registerUsernameUnderProperties;

    @NonNull
    Set<String> registerUsernameUnderEnvironmentVariables;

    @NonNull
    Set<String> registerPasswordUnderProperties;

    @NonNull
    Set<String> registerPasswordUnderEnvironmentVariables;

}
