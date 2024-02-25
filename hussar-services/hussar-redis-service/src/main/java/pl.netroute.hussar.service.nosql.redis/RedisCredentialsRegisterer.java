package pl.netroute.hussar.service.nosql.redis;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import pl.netroute.hussar.core.api.ConfigurationRegistry;
import pl.netroute.hussar.core.api.EnvVariableConfigurationEntry;
import pl.netroute.hussar.core.api.PropertyConfigurationEntry;
import pl.netroute.hussar.service.nosql.redis.api.RedisCredentials;

import java.util.Optional;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class RedisCredentialsRegisterer {

    @NonNull
    private final ConfigurationRegistry configurationRegistry;

    void registerUsernameUnderProperty(@NonNull RedisCredentials credentials,
                                       @NonNull String usernameProperty) {
        Optional
                .of(usernameProperty)
                .map(property -> new PropertyConfigurationEntry(property, credentials.username()))
                .ifPresent(configurationRegistry::register);
    }

    void registerPasswordUnderProperty(@NonNull RedisCredentials credentials,
                                       @NonNull String passwordProperty) {
        Optional
                .of(passwordProperty)
                .map(property -> new PropertyConfigurationEntry(property, credentials.password()))
                .ifPresent(configurationRegistry::register);
    }

    void registerUsernameUnderEnvironmentVariable(@NonNull RedisCredentials credentials,
                                                  @NonNull String usernameEnvVariable) {
        Optional
                .of(usernameEnvVariable)
                .map(envVariable -> new EnvVariableConfigurationEntry(envVariable, credentials.username()))
                .ifPresent(configurationRegistry::register);
    }

    void registerPasswordUnderEnvironmentVariable(@NonNull RedisCredentials credentials,
                                                  @NonNull String passwordEnvVariable) {
        Optional
                .of(passwordEnvVariable)
                .map(property -> new EnvVariableConfigurationEntry(property, credentials.password()))
                .ifPresent(configurationRegistry::register);
    }
}
