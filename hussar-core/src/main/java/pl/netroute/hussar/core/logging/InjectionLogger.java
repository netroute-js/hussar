package pl.netroute.hussar.core.logging;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import pl.netroute.hussar.core.api.InternalUseOnly;
import pl.netroute.hussar.core.application.api.Application;
import pl.netroute.hussar.core.helper.ClassHelper;
import pl.netroute.hussar.core.network.api.NetworkRestore;
import pl.netroute.hussar.core.service.api.Service;

import java.lang.reflect.Field;

@Slf4j
@InternalUseOnly
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class InjectionLogger {
    private static final String APPLICATION_INJECTION_TEMPLATE = "Injected Application [type: %s] -> %s.%s";
    private static final String SERVICE_INJECTION_TEMPLATE = "Injected Service [type: %s, name: %s] -> %s.%s";
    private static final String NETWORK_RESTORE_INJECTION_TEMPLATE = "Injected NetworkRestore [type: %s] -> %s.%s";

    public static void logApplicationInjected(@NonNull Object instance,
                                              @NonNull Field field,
                                              @NonNull Application application) {
        var applicationType = ClassHelper.toSimpleName(application);
        var instanceType = ClassHelper.toSimpleName(instance);
        var fieldName = field.getName();

        var logText = APPLICATION_INJECTION_TEMPLATE.formatted(applicationType, instanceType, fieldName);

        log.info(logText);
    }

    public static void logServiceInjected(@NonNull Object instance,
                                          @NonNull Field field,
                                          @NonNull Service service) {
        var serviceType = ClassHelper.toSimpleName(service);
        var serviceName = service.getName();
        var instanceType = ClassHelper.toSimpleName(instance);
        var fieldName = field.getName();

        var logText = SERVICE_INJECTION_TEMPLATE.formatted(serviceType, serviceName, instanceType, fieldName);

        log.info(logText);
    }

    public static void logNetworkRestoredInjected(@NonNull Object instance,
                                                  @NonNull Field field,
                                                  @NonNull NetworkRestore networkRestore) {
        var networkRestoreType = ClassHelper.toSimpleName(networkRestore);
        var instanceType = ClassHelper.toSimpleName(instance);
        var fieldName = field.getName();

        var logText = NETWORK_RESTORE_INJECTION_TEMPLATE.formatted(networkRestoreType, instanceType, fieldName);

        log.info(logText);
    }

}
