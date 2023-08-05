package pl.netroute.hussar.core.api;

import pl.netroute.hussar.core.helper.ValidatorHelper;

import java.util.Objects;

public class PropertyConfigurationEntry implements ConfigurationEntry {
    private final String name;
    private final String value;

    PropertyConfigurationEntry(String name, String value) {
        ValidatorHelper.requireNonEmpty(name, "name is required");

        this.name = name;
        this.value = value;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getFormattedName() {
        return name;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PropertyConfigurationEntry that = (PropertyConfigurationEntry) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

}
