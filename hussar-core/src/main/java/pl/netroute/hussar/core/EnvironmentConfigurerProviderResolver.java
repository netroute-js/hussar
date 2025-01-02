package pl.netroute.hussar.core;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import pl.netroute.hussar.core.environment.api.HussarEnvironment;
import pl.netroute.hussar.core.environment.api.EnvironmentConfigurerProvider;
import pl.netroute.hussar.core.helper.ReflectionHelper;

import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
class EnvironmentConfigurerProviderResolver {
    private static final Class<HussarEnvironment> TEST_CONFIGURER_ANNOTATION_CLASS = HussarEnvironment.class;

    Optional<EnvironmentConfigurerProvider> resolve(@NonNull Object testObject) {
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
