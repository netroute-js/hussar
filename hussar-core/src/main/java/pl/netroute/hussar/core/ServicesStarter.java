package pl.netroute.hussar.core;

import pl.netroute.hussar.core.api.Service;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;

class ServicesStarter {
    private static final Duration SERVICE_STARTUP_TIMEOUT = Duration.ofMinutes(1L);

    private final ExecutorService executorService;

    ServicesStarter(ExecutorService executorService) {
        Objects.requireNonNull(executorService, "executorService is required");

        this.executorService = executorService;
    }

    void start(ServicesConfiguration services) {
        Objects.requireNonNull(services, "services is required");

        startStandaloneServices(services.getStandaloneServices());
    }

    private void startStandaloneServices(List<Service> services) {
        services
                .stream()
                .map(service -> executorService.submit(service::start))
                .forEach(task -> FutureHelper.waitForTaskCompletion(task, SERVICE_STARTUP_TIMEOUT));
    }

}