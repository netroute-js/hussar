package pl.netroute.hussar.core.api;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class MapConfigurationRegistry implements ConfigurationRegistry {
    private final Set<ConfigurationEntry> registeredConfigurations;

    public MapConfigurationRegistry() {
        this(Set.of());
    }

    public MapConfigurationRegistry(Set<ConfigurationEntry> configurations) {
        Objects.requireNonNull(configurations, "configurations is required");

        this.registeredConfigurations = new HashSet<>(configurations);
    }

    @Override
    public void register(ConfigurationEntry configuration) {
        Objects.requireNonNull(configuration, "configuration is required");

        registeredConfigurations.add(configuration);
    }

    @Override
    public Set<ConfigurationEntry> getEntries() {
        return Set.copyOf(registeredConfigurations);
    }

}
