package pl.netroute.hussar.core;

import pl.netroute.hussar.core.api.Service;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;

class ServicesStopper {
    private static final Duration SERVICE_SHUTDOWN_TIMEOUT = Duration.ofMinutes(1L);

    private final ExecutorService executorService;

    ServicesStopper(ExecutorService executorService) {
        Objects.requireNonNull(executorService, "executorService is required");

        this.executorService = executorService;
    }

    void stop(ServicesConfiguration services) {
        Objects.requireNonNull(services, "services is required");

        stopStandaloneServices(services.getStandaloneServices());
    }

    private void stopStandaloneServices(List<Service> services) {
        services
                .stream()
                .map(service -> executorService.submit(service::shutdown))
                .forEach(task -> FutureHelper.waitForTaskCompletion(task, SERVICE_SHUTDOWN_TIMEOUT));
    }

}
