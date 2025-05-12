package pl.netroute.hussar.core.configuration.api;

import lombok.NonNull;

/**
 * Actual implementation of {@link ConfigurationEntry}. It represents the environment variable.
 */
public record EnvVariableConfigurationEntry(@NonNull String name,
                                            @NonNull String formattedName,
                                            @NonNull String value) implements ConfigurationEntry {
    private static final String ENV_VARIABLE_TEMPLATE = "${%s}";

    public EnvVariableConfigurationEntry(@NonNull String name,
                                         @NonNull String value) {
        this(name, resolveFormattedName(name), value);
    }

    private static String resolveFormattedName(String name) {
        return ENV_VARIABLE_TEMPLATE.formatted(name);
    }

}
