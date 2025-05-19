package pl.netroute.hussar.core;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import pl.netroute.hussar.core.api.InternalUseOnly;
import pl.netroute.hussar.core.application.api.Application;
import pl.netroute.hussar.core.application.api.HussarApplication;
import pl.netroute.hussar.core.environment.api.Environment;
import pl.netroute.hussar.core.helper.ReflectionHelper;
import pl.netroute.hussar.core.logging.InjectionLogger;

import java.lang.reflect.Field;

@InternalUseOnly
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
        ReflectionHelper.setValue(targetInstance, applicationField, application);

        InjectionLogger.logApplicationInjected(targetInstance, applicationField, application);
    }

    static ApplicationInjector newInstance(@NonNull Environment environment) {
        var application = environment.getApplication();

        return new ApplicationInjector(application);
    }

}
