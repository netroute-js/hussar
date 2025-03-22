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

    @NonNull
    Set<String> registerManagementEndpointUnderProperties;

    @NonNull
    Set<String> registerManagementEndpointUnderEnvironmentVariables;

    RabbitMQDockerServiceConfig(@NonNull RabbitMQDockerServiceConfig.RabbitMQDockerServiceConfigBuilder<?, ?> builder) {
        super(builder);

        if(!ManagementApiResolver.isSupported(this) && isManagementApiConfigurationPresent(builder)) {
            throw new IllegalArgumentException("Management API is disabled but Management API registration is required");
        }

        this.queues = builder.queues;
        this.registerUsernameUnderProperties = builder.registerUsernameUnderProperties;
        this.registerUsernameUnderEnvironmentVariables = builder.registerUsernameUnderEnvironmentVariables;
        this.registerPasswordUnderProperties = builder.registerPasswordUnderProperties;
        this.registerPasswordUnderEnvironmentVariables = builder.registerPasswordUnderEnvironmentVariables;
        this.registerManagementEndpointUnderProperties = builder.registerManagementEndpointUnderProperties;
        this.registerManagementEndpointUnderEnvironmentVariables = builder.registerManagementEndpointUnderEnvironmentVariables;
    }

    private boolean isManagementApiConfigurationPresent(RabbitMQDockerServiceConfig.RabbitMQDockerServiceConfigBuilder<?, ?> builder) {
        return !builder.registerManagementEndpointUnderProperties.isEmpty() || !builder.registerManagementEndpointUnderEnvironmentVariables.isEmpty();
    }

}
