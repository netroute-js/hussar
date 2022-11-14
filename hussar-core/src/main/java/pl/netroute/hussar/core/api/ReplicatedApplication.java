package pl.netroute.hussar.core.api;

import pl.netroute.hussar.core.Endpoint;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ReplicatedApplication implements Application {
    private final List<Application> replicas;

    ReplicatedApplication(List<Application> replicas) {
        Objects.requireNonNull(replicas, "replicas is required");

        if(replicas.isEmpty()) {
            throw new IllegalArgumentException("replicas is required");
        }

        this.replicas = replicas;
    }

    @Override
    public List<Endpoint> getEndpoints() {
        return replicas
                .stream()
                .map(Application::getEndpoints)
                .flatMap(Collection::stream)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public boolean isInitialized() {
        return replicas
                .stream()
                .allMatch(Application::isInitialized);
    }

    @Override
    public void start() {
        replicas.forEach(Application::start);
    }

    @Override
    public void shutdown() {
        replicas.forEach(Application::shutdown);
    }

    public static ReplicatedApplication newInstance(List<Application> replicas) {
        return new ReplicatedApplication(replicas);
    }

}
