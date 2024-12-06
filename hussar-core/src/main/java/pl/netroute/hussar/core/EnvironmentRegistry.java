package pl.netroute.hussar.core;

import lombok.NonNull;
import pl.netroute.hussar.core.api.environment.Environment;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

class EnvironmentRegistry {
    private final Map<Object, Environment> environments;

    EnvironmentRegistry() {
        this(new HashMap<>());
    }

    EnvironmentRegistry(@NonNull Map<Object, Environment> environments) {
        this.environments = new HashMap<>(environments);
    }

    void register(@NonNull Object testInstance, @NonNull Environment environment) {
        environments.putIfAbsent(testInstance, environment);
    }

    Optional<Environment> getEntry(@NonNull Object testInstance) {
        var environment = environments.get(testInstance);

        return Optional.ofNullable(environment);
    }

    Set<Environment> getEntries() {
        return Set.copyOf(environments.values());
    }

    void deleteEntries() {
        environments.clear();
    }

}
