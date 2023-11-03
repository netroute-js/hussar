package pl.netroute.hussar.core;

import pl.netroute.hussar.core.annotation.HussarEnvironment;
import pl.netroute.hussar.core.api.EnvironmentConfigurerProvider;
import pl.netroute.hussar.core.helper.ReflectionHelper;

import java.util.Objects;
import java.util.Optional;

class EnvironmentConfigurerProviderResolver {
    private static final Class<HussarEnvironment> TEST_CONFIGURER_ANNOTATION_CLASS = HussarEnvironment.class;

    Optional<EnvironmentConfigurerProvider> resolve(Object testObject) {
        Objects.requireNonNull("testObject is required");

        return fromAnnotation(testObject);
    }

    private Optional<EnvironmentConfigurerProvider> fromAnnotation(Object testObject) {
        return Optional
                .of(testObject)
                .map(Object::getClass)
                .filter(testType -> testType.isAnnotationPresent(TEST_CONFIGURER_ANNOTATION_CLASS))
                .map(testType -> testType.getAnnotation(TEST_CONFIGURER_ANNOTATION_CLASS))
                .map(HussarEnvironment::configurerProvider)
                .map(ReflectionHelper::newInstance);
    }

}
