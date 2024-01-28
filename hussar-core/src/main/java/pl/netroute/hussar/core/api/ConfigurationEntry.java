package pl.netroute.hussar.core.api;

import lombok.NonNull;

public interface ConfigurationEntry {
    String name();
    String formattedName();
    String value();

    static EnvVariableConfigurationEntry envVariable(@NonNull String name,
                                                     @NonNull String value) {
        return new EnvVariableConfigurationEntry(name, value);
    }

    static PropertyConfigurationEntry property(@NonNull String name,
                                               @NonNull String value) {
        return new PropertyConfigurationEntry(name, value);
    }

}
