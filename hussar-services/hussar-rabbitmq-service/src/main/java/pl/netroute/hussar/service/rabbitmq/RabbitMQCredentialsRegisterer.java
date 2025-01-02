package pl.netroute.hussar.service.rabbitmq;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import pl.netroute.hussar.core.configuration.api.ConfigurationRegistry;
import pl.netroute.hussar.core.configuration.api.EnvVariableConfigurationEntry;
import pl.netroute.hussar.core.configuration.api.PropertyConfigurationEntry;
import pl.netroute.hussar.service.rabbitmq.api.RabbitMQCredentials;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class RabbitMQCredentialsRegisterer {

    @NonNull
    private final ConfigurationRegistry configurationRegistry;

    void registerUsernameUnderProperty(@NonNull RabbitMQCredentials credentials,
                                       @NonNull String usernameProperty) {
        var property = new PropertyConfigurationEntry(usernameProperty, credentials.username());

        configurationRegistry.register(property);
    }

    void registerPasswordUnderProperty(@NonNull RabbitMQCredentials credentials,
                                       @NonNull String passwordProperty) {
        var property = new PropertyConfigurationEntry(passwordProperty, credentials.password());

        configurationRegistry.register(property);
    }

    void registerUsernameUnderEnvironmentVariable(@NonNull RabbitMQCredentials credentials,
                                                  @NonNull String usernameEnvVariable) {
        var envVariable=  new EnvVariableConfigurationEntry(usernameEnvVariable, credentials.username());

        configurationRegistry.register(envVariable);
    }

    void registerPasswordUnderEnvironmentVariable(@NonNull RabbitMQCredentials credentials,
                                                  @NonNull String passwordEnvVariable) {
        var envVariable = new EnvVariableConfigurationEntry(passwordEnvVariable, credentials.password());

        configurationRegistry.register(envVariable);
    }

}
