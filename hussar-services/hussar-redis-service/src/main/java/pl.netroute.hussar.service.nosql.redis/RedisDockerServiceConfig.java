package pl.netroute.hussar.service.nosql.redis;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.SuperBuilder;
import pl.netroute.hussar.core.helper.CollectionHelper;
import pl.netroute.hussar.core.service.BaseDockerServiceConfig;

import java.util.Set;

@Value
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
class RedisDockerServiceConfig extends BaseDockerServiceConfig {
    boolean enablePassword;

    @NonNull
    Set<String> registerUsernameUnderProperties;

    @NonNull
    Set<String> registerUsernameUnderEnvironmentVariables;

    @NonNull
    Set<String> registerPasswordUnderProperties;

    @NonNull
    Set<String> registerPasswordUnderEnvironmentVariables;

    protected RedisDockerServiceConfig(RedisDockerServiceConfig.RedisDockerServiceConfigBuilder<?, ?> builder) {
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

    private boolean isPasswordConfigurationValid(RedisDockerServiceConfig.RedisDockerServiceConfigBuilder<?, ?> builder) {
        var enablePassword = builder.enablePassword;
        var registerPasswordUnderProperties = builder.registerPasswordUnderProperties;
        var registerPasswordUnderEnvironmentVariables = builder.registerPasswordUnderEnvironmentVariables;

        return !enablePassword && (!CollectionHelper.isEmpty(registerPasswordUnderProperties) || !CollectionHelper.isEmpty(registerPasswordUnderEnvironmentVariables));
    }

}
