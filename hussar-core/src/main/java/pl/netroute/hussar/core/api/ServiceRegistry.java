package pl.netroute.hussar.core.api;

import lombok.NonNull;

import java.util.Optional;
import java.util.Set;

/**
 * Hussar interface responsible for maintaining {@link Service} configured in a given registry.
 */
public interface ServiceRegistry {
    /**
     * Registers a new {@link Service}.
     *
     * @param service - the {@link Service} to register.
     */
    void register(@NonNull Service service);

    /**
     * Returns all {@link Service}.
     *
     * @return all registered {@link Service}.
     */
    Set<Service> getEntries();

    /**
     * Find {@link Service} by name.
     *
     * @param name - the name of the {@link Service} to find.
     * @return the found {@link Service} or empty {@link Optional} if not found.
     */
    Optional<Service> findEntryByName(@NonNull String name);

    /**
     * Find {@link Service} by type.
     *
     * @param type - the type of the {@link Service} to find.
     * @return the found {@link Service} or empty {@link Optional} if not found.
     */
    Optional<Service> findEntryByType(@NonNull Class<? extends Service> type);
}
