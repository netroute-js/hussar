package pl.netroute.hussar.core.api;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class MapConfigurationRegistry implements ConfigurationRegistry {
    private final Set<ConfigurationEntry> registeredEntries;

    public MapConfigurationRegistry() {
        this.registeredEntries = new HashSet<>();
    }

    @Override
    public void register(ConfigurationEntry configurationEntry) {
        Objects.requireNonNull(configurationEntry, "configurationEntry is required");

        registeredEntries.add(configurationEntry);
    }

    @Override
    public Set<ConfigurationEntry> getEntries() {
        return Set.copyOf(registeredEntries);
    }

}
