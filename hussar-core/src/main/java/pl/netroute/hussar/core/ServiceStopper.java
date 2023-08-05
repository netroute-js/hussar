package pl.netroute.hussar.core;

import pl.netroute.hussar.core.api.Service;
import pl.netroute.hussar.core.api.ServiceRegistry;

import java.time.Duration;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;

class ServiceStopper {
    private static final Duration SERVICE_SHUTDOWN_TIMEOUT = Duration.ofMinutes(1L);

    private final ExecutorService executorService;

    ServiceStopper(ExecutorService executorService) {
        Objects.requireNonNull(executorService, "executorService is required");

        this.executorService = executorService;
    }

    void stop(ServiceRegistry serviceRegistry) {
        Objects.requireNonNull(serviceRegistry, "serviceRegistry is required");

        stopStandaloneServices(serviceRegistry.getEntries());
    }

    private void stopStandaloneServices(Set<Service> services) {
        services
                .stream()
                .map(service -> executorService.submit(service::shutdown))
                .forEach(task -> FutureHelper.waitForTaskCompletion(task, SERVICE_SHUTDOWN_TIMEOUT));
    }

}
