package pl.netroute.hussar.service.nosql.redis;

import lombok.EqualsAndHashCode;
import lombok.Singular;
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

    @Singular
    Set<String> registerUsernameUnderProperties;

    @Singular
    Set<String> registerUsernameUnderEnvironmentVariables;

    @Singular
    Set<String> registerPasswordUnderProperties;

    @Singular
    Set<String> registerPasswordUnderEnvironmentVariables;

    protected RedisDockerServiceConfig(RedisDockerServiceConfig.RedisDockerServiceConfigBuilder<?, ?> builder) {
        super(builder);

        if(isPasswordConfigurationValid(builder)) {
            throw new IllegalArgumentException("Redis password is disabled but password registration is required");
        }

        this.enablePassword = builder.enablePassword;
        this.registerUsernameUnderProperties = CollectionHelper.getSetOrEmpty(builder.registerUsernameUnderProperties);
        this.registerUsernameUnderEnvironmentVariables = CollectionHelper.getSetOrEmpty(builder.registerUsernameUnderEnvironmentVariables);
        this.registerPasswordUnderProperties = CollectionHelper.getSetOrEmpty(builder.registerPasswordUnderProperties);
        this.registerPasswordUnderEnvironmentVariables = CollectionHelper.getSetOrEmpty(builder.registerPasswordUnderEnvironmentVariables);
    }

    private boolean isPasswordConfigurationValid(RedisDockerServiceConfig.RedisDockerServiceConfigBuilder<?, ?> builder) {
        var enablePassword = builder.enablePassword;
        var registerPasswordUnderProperties = builder.registerPasswordUnderProperties;
        var registerPasswordUnderEnvironmentVariables = builder.registerPasswordUnderEnvironmentVariables;

        return !enablePassword && (!CollectionHelper.isEmpty(registerPasswordUnderProperties) || !CollectionHelper.isEmpty(registerPasswordUnderEnvironmentVariables));
    }

}
