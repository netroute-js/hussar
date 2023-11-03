package pl.netroute.hussar.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.netroute.hussar.core.annotation.HussarApplication;
import pl.netroute.hussar.core.api.Application;
import pl.netroute.hussar.core.helper.ReflectionHelper;

import java.lang.reflect.Field;
import java.util.Objects;

class ApplicationInjector {
    private static final Logger LOG = LoggerFactory.getLogger(ServiceInjector.class);

    private static final Class<HussarApplication> HUSSAR_APPLICATION_ANNOTATION_CLASS = HussarApplication.class;

    private final Application application;

    ApplicationInjector(Application application) {
        Objects.requireNonNull(application, "application is required");

        this.application = application;
    }

    void inject(Object targetInstance) {
        Objects.requireNonNull(targetInstance, "targetInstance is required");

        ReflectionHelper
                .getFieldsAnnotatedWith(targetInstance, HUSSAR_APPLICATION_ANNOTATION_CLASS)
                .forEach(serviceField -> injectApplication(targetInstance, serviceField));
    }

    private void injectApplication(Object targetInstance, Field applicationField) {
        LOG.info("Injecting {} into {}", application.getClass().getSimpleName(), targetInstance.getClass().getSimpleName());

        ReflectionHelper.setValue(targetInstance, applicationField, application);
    }

    static ApplicationInjector newInstance(Environment environment) {
        Objects.requireNonNull(environment, "environment is required");

        var application = environment.getApplication();

        return new ApplicationInjector(application);
    }
}
