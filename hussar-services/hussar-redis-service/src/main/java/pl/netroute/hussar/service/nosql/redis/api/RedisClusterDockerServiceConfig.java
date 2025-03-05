package pl.netroute.hussar.service.nosql.redis.api;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.SuperBuilder;
import pl.netroute.hussar.core.api.InternalUseOnly;
import pl.netroute.hussar.core.helper.CollectionHelper;
import pl.netroute.hussar.core.service.api.BaseDockerServiceConfig;

import java.util.Set;

@Value
@SuperBuilder
@InternalUseOnly
@EqualsAndHashCode(callSuper = true)
class RedisClusterDockerServiceConfig extends BaseDockerServiceConfig {
    boolean enablePassword;

    @NonNull
    Set<String> registerUsernameUnderProperties;

    @NonNull
    Set<String> registerUsernameUnderEnvironmentVariables;

    @NonNull
    Set<String> registerPasswordUnderProperties;

    @NonNull
    Set<String> registerPasswordUnderEnvironmentVariables;

    protected RedisClusterDockerServiceConfig(@NonNull RedisClusterDockerServiceConfig.RedisClusterDockerServiceConfigBuilder<?, ?> builder) {
        super(builder);

        if(isPasswordConfigurationValid(builder)) {
            throw new IllegalArgumentException("Redis password is disabled but password registration is required");
        }

        this.enablePassword = builder.enablePassword;
        this.registerUsernameUnderProperties = builder.registerUsernameUnderProperties;
        this.registerUsernameUnderEnvironmentVariables = builder.registerUsernameUnderEnvironmentVariables;
        this.registerPasswordUnderProperties = builder.registerPasswordUnderProperties;
        this.registerPasswordUnderEnvironmentVariables = builder.registerPasswordUnderEnvironmentVariables;
    }

    private boolean isPasswordConfigurationValid(RedisClusterDockerServiceConfig.RedisClusterDockerServiceConfigBuilder<?, ?> builder) {
        var enablePassword = builder.enablePassword;
        var registerPasswordUnderProperties = builder.registerPasswordUnderProperties;
        var registerPasswordUnderEnvironmentVariables = builder.registerPasswordUnderEnvironmentVariables;

        return !enablePassword && (!CollectionHelper.isEmpty(registerPasswordUnderProperties) || !CollectionHelper.isEmpty(registerPasswordUnderEnvironmentVariables));
    }

}
