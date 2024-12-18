package pl.netroute.hussar.spring.boot;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import pl.netroute.hussar.core.api.Endpoint;
import pl.netroute.hussar.core.api.application.Application;
import pl.netroute.hussar.core.api.application.ApplicationStartupContext;

import java.util.Collection;
import java.util.List;
import java.util.stream.IntStream;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ClusterSpringBootApplication implements Application {

    @NonNull
    private final List<SpringBootApplication> applications;

    @Override
    public List<Endpoint> getEndpoints() {
        return applications
                .stream()
                .map(Application::getEndpoints)
                .flatMap(Collection::stream)
                .toList();
    }

    @Override
    public boolean isInitialized() {
        return applications
                .stream()
                .allMatch(Application::isInitialized);
    }

    @Override
    public void start(@NonNull ApplicationStartupContext context) {
        applications.forEach(application -> application.start(context));
    }

    @Override
    public void shutdown() {
        applications.forEach(Application::shutdown);
    }

    @Override
    public void restart() {
        applications.forEach(Application::restart);
    }

    public static ClusterSpringBootApplication newApplication(int replicas, @NonNull Class<?> applicationClass) {
        if(replicas <= 1) {
            throw new IllegalArgumentException("replicas must be greater than 1");
        }

        var applications = IntStream
                .range(0, replicas)
                .mapToObj(index -> SpringBootApplication.newApplication(applicationClass))
                .toList();

        return new ClusterSpringBootApplication(applications);
    }

}
