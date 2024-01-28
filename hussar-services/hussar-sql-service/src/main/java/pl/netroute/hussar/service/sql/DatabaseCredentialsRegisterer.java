package pl.netroute.hussar.service.sql;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import pl.netroute.hussar.core.api.ConfigurationRegistry;
import pl.netroute.hussar.core.api.EnvVariableConfigurationEntry;
import pl.netroute.hussar.core.api.PropertyConfigurationEntry;

import java.util.Optional;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class DatabaseCredentialsRegisterer {
    private final ConfigurationRegistry configurationRegistry;

    void registerUsernameUnderProperty(@NonNull DatabaseCredentials credentials,
                                       @NonNull String usernameProperty) {
        Optional
                .of(usernameProperty)
                .map(property -> new PropertyConfigurationEntry(property, credentials.username()))
                .ifPresent(configurationRegistry::register);
    }

    void registerPasswordUnderProperty(@NonNull DatabaseCredentials credentials,
                                       @NonNull String passwordProperty) {
        Optional
                .ofNullable(passwordProperty)
                .map(property -> new PropertyConfigurationEntry(property, credentials.password()))
                .ifPresent(configurationRegistry::register);
    }

    void registerUsernameUnderEnvironmentVariable(@NonNull DatabaseCredentials credentials,
                                                  @NonNull String usernameEnvVariable) {
        Optional
                .of(usernameEnvVariable)
                .map(envVariable -> new EnvVariableConfigurationEntry(envVariable, credentials.username()))
                .ifPresent(configurationRegistry::register);
    }

    void registerPasswordUnderEnvironmentVariable(@NonNull DatabaseCredentials credentials,
                                                  @NonNull String passwordEnvVariable) {
        Optional
                .ofNullable(passwordEnvVariable)
                .map(property -> new EnvVariableConfigurationEntry(property, credentials.password()))
                .ifPresent(configurationRegistry::register);
    }
}
