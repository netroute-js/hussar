package pl.netroute.hussar.core;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.netroute.hussar.core.annotation.HussarService;
import pl.netroute.hussar.core.api.Environment;
import pl.netroute.hussar.core.api.Service;
import pl.netroute.hussar.core.api.ServiceRegistry;
import pl.netroute.hussar.core.helper.ReflectionHelper;

import java.lang.reflect.Field;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class ServiceInjector {
    private static final Class<HussarService> HUSSAR_SERVICE_ANNOTATION_CLASS = HussarService.class;

    @NonNull
    private final ServiceRegistry serviceRegistry;

    void inject(@NonNull Object targetInstance) {
        injectServices(targetInstance);
    }

    private void injectServices(Object targetInstance) {
        ReflectionHelper
                .getFieldsAnnotatedWith(targetInstance, HUSSAR_SERVICE_ANNOTATION_CLASS)
                .forEach(serviceField -> injectService(targetInstance, serviceField));
    }

    private void injectService(Object targetInstance, Field serviceField) {
        var service = serviceField.getAnnotation(HUSSAR_SERVICE_ANNOTATION_CLASS);

        Optional
                .of(service)
                .filter(this::isInjectByType)
                .ifPresentOrElse(
                        actualService -> injectServiceByType(targetInstance, serviceField),
                        () -> injectServiceByName(targetInstance, serviceField, service)
                );
    }

    private boolean isInjectByType(HussarService service) {
        String name = service.name();

        return name == null || name.isBlank();
    }

    private void injectServiceByType(Object targetInstance, Field serviceField) {
        var serviceType = (Class<? extends Service>) serviceField.getType();

        Optional
                .of(serviceType)
                .flatMap(serviceRegistry::findEntryByType)
                .ifPresentOrElse(
                        service -> doServiceInjection(targetInstance, serviceField, service),
                        () -> { throw new IllegalStateException(String.format("Expected exactly one HussarService of %s type", serviceType)); }
                );
    }

    private void injectServiceByName(Object targetInstance, Field serviceField, HussarService service) {
        String serviceName = service.name();

        Optional
                .of(serviceName)
                .flatMap(serviceRegistry::findEntryByName)
                .ifPresentOrElse(
                        actualService -> doServiceInjection(targetInstance, serviceField, actualService),
                        () -> { throw new IllegalStateException(String.format("Expected exactly one HussarService named %s", serviceName)); }
                );

    }

    private void doServiceInjection(Object targetInstance, Field serviceField, Service service) {
        log.info("Injecting {} into {}", service.getClass().getSimpleName(), targetInstance.getClass().getSimpleName());

        ReflectionHelper.setValue(targetInstance, serviceField, service);
    }

    static ServiceInjector newInstance(@NonNull Environment environment) {
        var serviceRegistry = environment.serviceRegistry();

        return new ServiceInjector(serviceRegistry);
    }
}
