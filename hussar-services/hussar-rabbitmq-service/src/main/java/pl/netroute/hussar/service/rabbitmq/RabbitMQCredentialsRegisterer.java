package pl.netroute.hussar.service.rabbitmq;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import pl.netroute.hussar.core.api.configuration.ConfigurationRegistry;
import pl.netroute.hussar.core.api.configuration.EnvVariableConfigurationEntry;
import pl.netroute.hussar.core.api.configuration.PropertyConfigurationEntry;
import pl.netroute.hussar.service.rabbitmq.api.RabbitMQCredentials;

import java.util.Optional;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class RabbitMQCredentialsRegisterer {

    @NonNull
    private final ConfigurationRegistry configurationRegistry;

    void registerUsernameUnderProperty(@NonNull RabbitMQCredentials credentials,
                                       @NonNull String usernameProperty) {
        Optional
                .of(usernameProperty)
                .map(property -> new PropertyConfigurationEntry(property, credentials.username()))
                .ifPresent(configurationRegistry::register);
    }

    void registerPasswordUnderProperty(@NonNull RabbitMQCredentials credentials,
                                       @NonNull String passwordProperty) {
        Optional
                .of(passwordProperty)
                .map(property -> new PropertyConfigurationEntry(property, credentials.password()))
                .ifPresent(configurationRegistry::register);
    }

    void registerUsernameUnderEnvironmentVariable(@NonNull RabbitMQCredentials credentials,
                                                  @NonNull String usernameEnvVariable) {
        Optional
                .of(usernameEnvVariable)
                .map(envVariable -> new EnvVariableConfigurationEntry(envVariable, credentials.username()))
                .ifPresent(configurationRegistry::register);
    }

    void registerPasswordUnderEnvironmentVariable(@NonNull RabbitMQCredentials credentials,
                                                  @NonNull String passwordEnvVariable) {
        Optional
                .of(passwordEnvVariable)
                .map(property -> new EnvVariableConfigurationEntry(property, credentials.password()))
                .ifPresent(configurationRegistry::register);
    }
}
