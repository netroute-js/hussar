package pl.netroute.hussar.core.api;

import pl.netroute.hussar.core.helper.ValidatorHelper;

import java.util.Objects;

public class EnvVariableConfigurationEntry implements ConfigurationEntry {

    private static final String ENV_VARIABLE_TEMPLATE = "${%s}";

    private final String name;
    private final String formattedName;
    private final String value;

    EnvVariableConfigurationEntry(String name, String value) {
        ValidatorHelper.requireNonEmpty(name, "name is required");
        ValidatorHelper.requireNonEmpty(value, "value is required");

        this.name = name;
        this.formattedName = String.format(ENV_VARIABLE_TEMPLATE, name);
        this.value = value;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getFormattedName() {
        return formattedName;
    }

    @Override
    public String getValue() {
        return value;
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

}
