package pl.netroute.hussar.core;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import pl.netroute.hussar.core.api.service.Service;
import pl.netroute.hussar.core.api.service.ServiceRegistry;
import pl.netroute.hussar.core.api.service.ServiceStartupContext;
import pl.netroute.hussar.core.helper.FutureHelper;

import java.time.Duration;
import java.util.Set;
import java.util.concurrent.ExecutorService;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class ServiceStarter {
    private static final Duration SERVICE_STARTUP_TIMEOUT = Duration.ofMinutes(2L);

    @NonNull
    private final ExecutorService executorService;

    void start(@NonNull ServiceRegistry serviceRegistry) {
        startStandaloneServices(serviceRegistry.getEntries());
    }

    private void startStandaloneServices(Set<Service> services) {
        services
                .stream()
                .map(service -> executorService.submit(() -> service.start(ServiceStartupContext.empty())))
                .forEach(task -> FutureHelper.waitForTaskCompletion(task, SERVICE_STARTUP_TIMEOUT));
    }

}