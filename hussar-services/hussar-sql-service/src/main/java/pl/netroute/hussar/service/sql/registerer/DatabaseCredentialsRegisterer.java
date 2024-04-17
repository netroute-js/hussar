package pl.netroute.hussar.service.sql.registerer;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import pl.netroute.hussar.core.api.ConfigurationRegistry;
import pl.netroute.hussar.core.api.EnvVariableConfigurationEntry;
import pl.netroute.hussar.core.api.PropertyConfigurationEntry;
import pl.netroute.hussar.service.sql.api.SQLDatabaseCredentials;

import java.util.Optional;

@RequiredArgsConstructor
public class DatabaseCredentialsRegisterer {

    @NonNull
    private final ConfigurationRegistry configurationRegistry;

    public void registerUsernameUnderProperty(@NonNull SQLDatabaseCredentials credentials,
                                              @NonNull String usernameProperty) {
        Optional
                .of(usernameProperty)
                .map(property -> new PropertyConfigurationEntry(property, credentials.username()))
                .ifPresent(configurationRegistry::register);
    }

    public void registerPasswordUnderProperty(@NonNull SQLDatabaseCredentials credentials,
                                              @NonNull String passwordProperty) {
        Optional
                .of(passwordProperty)
                .map(property -> new PropertyConfigurationEntry(property, credentials.password()))
                .ifPresent(configurationRegistry::register);
    }

    public void registerUsernameUnderEnvironmentVariable(@NonNull SQLDatabaseCredentials credentials,
                                                         @NonNull String usernameEnvVariable) {
        Optional
                .of(usernameEnvVariable)
                .map(envVariable -> new EnvVariableConfigurationEntry(envVariable, credentials.username()))
                .ifPresent(configurationRegistry::register);
    }

    public void registerPasswordUnderEnvironmentVariable(@NonNull SQLDatabaseCredentials credentials,
                                                         @NonNull String passwordEnvVariable) {
        Optional
                .of(passwordEnvVariable)
                .map(property -> new EnvVariableConfigurationEntry(property, credentials.password()))
                .ifPresent(configurationRegistry::register);
    }
}
