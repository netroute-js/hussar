package pl.netroute.hussar.core;

import pl.netroute.hussar.core.api.Service;
import pl.netroute.hussar.core.api.ServiceRegistry;
import pl.netroute.hussar.core.api.ServiceStartupContext;

import java.time.Duration;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;

class ServiceStarter {
    private static final Duration SERVICE_STARTUP_TIMEOUT = Duration.ofMinutes(1L);

    private final ExecutorService executorService;

    ServiceStarter(ExecutorService executorService) {
        Objects.requireNonNull(executorService, "executorService is required");

        this.executorService = executorService;
    }

    void start(ServiceRegistry serviceRegistry) {
        Objects.requireNonNull(serviceRegistry, "serviceRegistry is required");

        startStandaloneServices(serviceRegistry.getEntries());
    }

    private void startStandaloneServices(Set<Service> services) {
        services
                .stream()
                .map(service -> executorService.submit(() -> service.start(ServiceStartupContext.empty())))
                .forEach(task -> FutureHelper.waitForTaskCompletion(task, SERVICE_STARTUP_TIMEOUT));
    }

}