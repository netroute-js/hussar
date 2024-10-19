package pl.netroute.hussar.core.api.configuration;

import lombok.NonNull;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EnvVariableConfigurationEntry that = (EnvVariableConfigurationEntry) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    private static String resolveFormattedName(String name) {
        return ENV_VARIABLE_TEMPLATE.formatted(name);
    }

}
