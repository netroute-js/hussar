package pl.netroute.hussar.core.service.api;

import pl.netroute.hussar.core.api.ConfigurationEntry;

public interface RegistrableConfigurationEntry {
    String getName();

    ConfigurationEntry toResolvedConfigurationEntry(String value);

    static RegistrableEnvVariableConfigurationEntry envVariable(String name) {
        return new RegistrableEnvVariableConfigurationEntry(name);
    }

    static RegistrablePropertyConfigurationEntry property(String name) {
        return new RegistrablePropertyConfigurationEntry(name);
    }

}
