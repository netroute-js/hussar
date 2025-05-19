package pl.netroute.hussar.core.service.logger;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import pl.netroute.hussar.core.api.InternalUseOnly;
import pl.netroute.hussar.core.helper.ClassHelper;
import pl.netroute.hussar.core.service.api.BaseDockerServiceConfig;
import pl.netroute.hussar.core.service.api.BaseServiceConfig;
import pl.netroute.hussar.core.service.api.Service;

@Slf4j
@InternalUseOnly
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ServiceLogger {
    private static final String SERVICE_STARTUP_TEMPLATE = "Started Service [type: %s, name: %s]";
    private static final String DOCKER_SERVICE_STARTUP_TEMPLATE = "Started Service [type: %s, name: %s, dockerImage: %s]";
    private static final String SERVICE_SHUTDOWN_TEMPLATE = "Shutdown Service [type: %s, name: %s]";

    public static void logServiceStartup(@NonNull Service service, @NonNull BaseServiceConfig config) {
        var serviceType = ClassHelper.toSimpleName(service);
        var serviceName = service.getName();

        String logText;
        if(config instanceof BaseDockerServiceConfig dockerConfig) {
            var dockerImage = dockerConfig.getDockerImage();

            logText = DOCKER_SERVICE_STARTUP_TEMPLATE.formatted(serviceType, serviceName, dockerImage);
        } else {
            logText = SERVICE_STARTUP_TEMPLATE.formatted(serviceType, serviceName);
        }

        log.info(logText);
    }

    public static void logServiceShutdown(@NonNull Service service) {
        var serviceType = ClassHelper.toSimpleName(service);
        var serviceName = service.getName();

        var logText = SERVICE_SHUTDOWN_TEMPLATE.formatted(serviceType, serviceName);

        log.info(logText);
    }

}
