package pl.netroute.hussar.core;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.netroute.hussar.core.application.api.HussarApplication;
import pl.netroute.hussar.core.application.api.Application;
import pl.netroute.hussar.core.environment.api.Environment;
import pl.netroute.hussar.core.helper.ReflectionHelper;

import java.lang.reflect.Field;

@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class ApplicationInjector {
    private static final Class<HussarApplication> HUSSAR_APPLICATION_ANNOTATION_CLASS = HussarApplication.class;

    @NonNull
    private final Application application;

    void inject(@NonNull Object targetInstance) {
        ReflectionHelper
                .getFieldsAnnotatedWith(targetInstance, HUSSAR_APPLICATION_ANNOTATION_CLASS)
                .forEach(serviceField -> injectApplication(targetInstance, serviceField));
    }

    private void injectApplication(Object targetInstance, Field applicationField) {
        log.info("Injecting {} into {}", application.getClass().getSimpleName(), targetInstance.getClass().getSimpleName());

        ReflectionHelper.setValue(targetInstance, applicationField, application);
    }

    static ApplicationInjector newInstance(@NonNull Environment environment) {
        var application = environment.application();

        return new ApplicationInjector(application);
    }

}
