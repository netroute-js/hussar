package pl.netroute.hussar.core.configuration.api;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.Set;

/**
 * An actual implementation of {@link ConfigurationRegistry}.
 */
@RequiredArgsConstructor
public class DefaultConfigurationRegistry implements ConfigurationRegistry {

    @NonNull
    private final Set<ConfigurationEntry> registeredConfigurations;

    /**
     * Creates new instance of {@link DefaultConfigurationRegistry}.
     */
    public DefaultConfigurationRegistry() {
        this(new HashSet<>());
    }

    @Override
    public void register(@NonNull ConfigurationEntry configuration) {
        registeredConfigurations
                .stream()
                .filter(actualConfiguration -> actualConfiguration.name().equals(configuration.name()))
                .findFirst()
                .ifPresentOrElse(
                        actualConfiguration -> {},
                        () -> registeredConfigurations.add(configuration)
                );
    }

    @Override
    public Set<ConfigurationEntry> getEntries() {
        return Set.copyOf(registeredConfigurations);
    }

}
