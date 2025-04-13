package pl.netroute.hussar.core.environment;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import pl.netroute.hussar.core.api.InternalUseOnly;
import pl.netroute.hussar.core.helper.FutureHelper;
import pl.netroute.hussar.core.service.ServiceStartupContext;
import pl.netroute.hussar.core.service.api.Service;
import pl.netroute.hussar.core.service.api.ServiceRegistry;

import java.time.Duration;
import java.util.Set;
import java.util.concurrent.ExecutorService;

@InternalUseOnly
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class ServiceStarter {
    private static final Duration SERVICE_STARTUP_TIMEOUT = Duration.ofMinutes(10L);

    @NonNull
    private final ExecutorService executorService;

    void start(@NonNull ServiceRegistry serviceRegistry) {
        startStandaloneServices(serviceRegistry.getEntries());
    }

    private void startStandaloneServices(Set<Service> services) {
        var startTasks = services
                .stream()
                .map(service -> executorService.submit(() -> service.start(ServiceStartupContext.defaultContext())))
                .toList();

        startTasks.forEach(task -> FutureHelper.waitForTaskCompletion(task, SERVICE_STARTUP_TIMEOUT));
    }

}