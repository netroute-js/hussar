package pl.netroute.hussar.core.test.factory;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.netroute.hussar.core.configuration.api.ConfigurationRegistry;
import pl.netroute.hussar.core.configuration.api.DefaultConfigurationRegistry;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ConfigurationRegistryTestFactory {

    public static ConfigurationRegistry create() {
        var property = ConfigurationEntryTestFactory.createProperty();
        var envVariable = ConfigurationEntryTestFactory.createEnvVariable();

        return DefaultConfigurationRegistry.of(property, envVariable);
    }

}
