package pl.netroute.hussar.core;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import pl.netroute.hussar.core.api.InternalUseOnly;
import pl.netroute.hussar.core.service.api.Service;
import pl.netroute.hussar.core.service.api.ServiceRegistry;
import pl.netroute.hussar.core.helper.FutureHelper;

import java.time.Duration;
import java.util.Set;
import java.util.concurrent.ExecutorService;

@InternalUseOnly
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class ServiceStopper {
    private static final Duration SERVICE_SHUTDOWN_TIMEOUT = Duration.ofMinutes(1L);

    @NonNull
    private final ExecutorService executorService;

    void stop(@NonNull ServiceRegistry serviceRegistry) {
        stopStandaloneServices(serviceRegistry.getEntries());
    }

    private void stopStandaloneServices(Set<Service> services) {
        services
                .stream()
                .map(service -> executorService.submit(service::shutdown))
                .forEach(task -> FutureHelper.waitForTaskCompletion(task, SERVICE_SHUTDOWN_TIMEOUT));
    }

}
