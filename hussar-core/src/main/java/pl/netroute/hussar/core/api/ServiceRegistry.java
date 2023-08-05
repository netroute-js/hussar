package pl.netroute.hussar.core.api;

import java.util.Optional;
import java.util.Set;

public interface ServiceRegistry {
    void register(Service service);
    Set<Service> getEntries();

    Optional<Service> findEntryByName(String name);
    Optional<Service> findEntryByType(Class<? extends Service> type);
}
