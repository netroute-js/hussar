package pl.netroute.hussar.core.api;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.Set;

/**
 * An actual implementation of {@link ConfigurationRegistry}.
 */
@RequiredArgsConstructor
public class MapConfigurationRegistry implements ConfigurationRegistry {

    @NonNull
    private final Set<ConfigurationEntry> registeredConfigurations;

    /**
     * Creates new instance of {@link MapConfigurationRegistry}.
     */
    public MapConfigurationRegistry() {
        this(new HashSet<>());
    }

    @Override
    public void register(@NonNull ConfigurationEntry configuration) {
        registeredConfigurations.add(configuration);
    }

    @Override
    public Set<ConfigurationEntry> getEntries() {
        return Set.copyOf(registeredConfigurations);
    }

}
