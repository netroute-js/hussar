package pl.netroute.hussar.core.api;

import java.util.Set;

public interface ConfigurationRegistry {
    void register(ConfigurationEntry entry);

    Set<ConfigurationEntry> getEntries();

}
