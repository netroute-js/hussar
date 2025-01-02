package pl.netroute.hussar.core.configuration.api;

import lombok.NonNull;

/**
 * Hussar interface that is responsible for exposing methods for configuration entry.
 */
public interface ConfigurationEntry {

    /**
     * Returns name of {@link ConfigurationEntry}.
     *
     * @return the actual name of the {@link ConfigurationEntry}.
     */
    String name();

    /**
     * Returns formatted name of {@link ConfigurationEntry}.
     *
     * @return the actual formatted name of {@link ConfigurationEntry}.
     */
    String formattedName();

    /**
     * Returns value of {@link ConfigurationEntry}.
     *
     * @return the actual value of {@link ConfigurationEntry}.
     */
    String value();

    /**
     * Factory method to create {@link EnvVariableConfigurationEntry}.
     *
     * @param name - the name of {@link EnvVariableConfigurationEntry}.
     * @param value - the value of {@link EnvVariableConfigurationEntry}.
     * @return created {@link EnvVariableConfigurationEntry}.
     */
    static EnvVariableConfigurationEntry envVariable(@NonNull String name,
                                                     @NonNull String value) {
        return new EnvVariableConfigurationEntry(name, value);
    }

    /**
     * Factory method to create {@link PropertyConfigurationEntry}.
     *
     * @param name - the name of {@link PropertyConfigurationEntry}.
     * @param value - the value of {@link PropertyConfigurationEntry}.
     * @return created {@link PropertyConfigurationEntry}.
     */
    static PropertyConfigurationEntry property(@NonNull String name,
                                               @NonNull String value) {
        return new PropertyConfigurationEntry(name, value);
    }

}
