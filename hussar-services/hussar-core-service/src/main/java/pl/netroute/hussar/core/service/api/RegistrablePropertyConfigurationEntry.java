package pl.netroute.hussar.core.service.api;

import pl.netroute.hussar.core.api.ConfigurationEntry;
import pl.netroute.hussar.core.helper.ValidatorHelper;

import java.util.Objects;

public class RegistrablePropertyConfigurationEntry implements RegistrableConfigurationEntry {
    private final String name;

    RegistrablePropertyConfigurationEntry(String name) {
        ValidatorHelper.requireNonEmpty(name, "name is required");

        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ConfigurationEntry toResolvedConfigurationEntry(String value) {
        ValidatorHelper.requireNonEmpty(value, "value is required");

        return ConfigurationEntry.property(name, value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegistrablePropertyConfigurationEntry that = (RegistrablePropertyConfigurationEntry) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

}
