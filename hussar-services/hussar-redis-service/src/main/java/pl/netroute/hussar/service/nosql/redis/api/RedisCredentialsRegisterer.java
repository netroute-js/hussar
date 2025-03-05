package pl.netroute.hussar.service.nosql.redis.api;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import pl.netroute.hussar.core.api.InternalUseOnly;
import pl.netroute.hussar.core.configuration.api.ConfigurationRegistry;
import pl.netroute.hussar.core.configuration.api.EnvVariableConfigurationEntry;
import pl.netroute.hussar.core.configuration.api.PropertyConfigurationEntry;

import java.util.Optional;

@InternalUseOnly
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class RedisCredentialsRegisterer {

    @NonNull
    private final ConfigurationRegistry configurationRegistry;

    void registerUsernameUnderProperty(@NonNull RedisCredentials credentials,
                                       @NonNull String usernameProperty) {
        var property = new PropertyConfigurationEntry(usernameProperty, credentials.username());

        configurationRegistry.register(property);
    }

    void registerPasswordUnderProperty(@NonNull RedisCredentials credentials,
                                       @NonNull String passwordProperty) {
        Optional
                .ofNullable(credentials.password())
                .map(password -> new PropertyConfigurationEntry(passwordProperty, password))
                .ifPresent(configurationRegistry::register);
    }

    void registerUsernameUnderEnvironmentVariable(@NonNull RedisCredentials credentials,
                                                  @NonNull String usernameEnvVariable) {
        var envVariable = new EnvVariableConfigurationEntry(usernameEnvVariable, credentials.username());

        configurationRegistry.register(envVariable);
    }

    void registerPasswordUnderEnvironmentVariable(@NonNull RedisCredentials credentials,
                                                  @NonNull String passwordEnvVariable) {
        Optional
                .ofNullable(credentials.password())
                .map(password -> new EnvVariableConfigurationEntry(passwordEnvVariable, password))
                .ifPresent(configurationRegistry::register);
    }

}
