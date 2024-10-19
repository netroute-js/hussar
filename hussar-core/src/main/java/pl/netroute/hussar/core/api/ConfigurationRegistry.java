package pl.netroute.hussar.core.api;

import lombok.NonNull;

import java.util.Set;

/**
 * Hussar interface responsible for maintaining {@link ConfigurationEntry} configured in a given registry.
 */
public interface ConfigurationRegistry {

    /**
     * Registers new {@link ConfigurationEntry}.
     *
     * @param entry - the {@link ConfigurationEntry} to register
     */
    void register(@NonNull ConfigurationEntry entry);

    /**
     * Returns all entries.
     *
     * @return all registered {@link ConfigurationEntry}.
     */
    Set<ConfigurationEntry> getEntries();

}
