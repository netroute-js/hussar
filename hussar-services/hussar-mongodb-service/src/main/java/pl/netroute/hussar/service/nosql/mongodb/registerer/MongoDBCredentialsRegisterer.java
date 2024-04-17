package pl.netroute.hussar.service.nosql.mongodb.registerer;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import pl.netroute.hussar.core.api.ConfigurationRegistry;
import pl.netroute.hussar.core.api.EnvVariableConfigurationEntry;
import pl.netroute.hussar.core.api.PropertyConfigurationEntry;
import pl.netroute.hussar.service.nosql.mongodb.api.MongoDBCredentials;

import java.util.Optional;

@RequiredArgsConstructor
public class MongoDBCredentialsRegisterer {

    @NonNull
    private final ConfigurationRegistry configurationRegistry;

    public void registerUsernameUnderProperty(@NonNull MongoDBCredentials credentials,
                                              @NonNull String usernameProperty) {
        Optional
                .of(usernameProperty)
                .map(property -> new PropertyConfigurationEntry(property, credentials.username()))
                .ifPresent(configurationRegistry::register);
    }

    public void registerPasswordUnderProperty(@NonNull MongoDBCredentials credentials,
                                              @NonNull String passwordProperty) {
        Optional
                .of(passwordProperty)
                .map(property -> new PropertyConfigurationEntry(property, credentials.password()))
                .ifPresent(configurationRegistry::register);
    }

    public void registerUsernameUnderEnvironmentVariable(@NonNull MongoDBCredentials credentials,
                                                         @NonNull String usernameEnvVariable) {
        Optional
                .of(usernameEnvVariable)
                .map(envVariable -> new EnvVariableConfigurationEntry(envVariable, credentials.username()))
                .ifPresent(configurationRegistry::register);
    }

    public void registerPasswordUnderEnvironmentVariable(@NonNull MongoDBCredentials credentials,
                                                         @NonNull String passwordEnvVariable) {
        Optional
                .of(passwordEnvVariable)
                .map(property -> new EnvVariableConfigurationEntry(property, credentials.password()))
                .ifPresent(configurationRegistry::register);
    }
}
