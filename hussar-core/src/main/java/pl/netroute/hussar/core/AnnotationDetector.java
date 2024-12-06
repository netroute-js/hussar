package pl.netroute.hussar.core;

import lombok.NonNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.function.Consumer;

class AnnotationDetector {

    <A extends Annotation> void detect(@NonNull Method testMethod,
                                       @NonNull Class<A> annotationType,
                                       @NonNull Consumer<A> onDetected) {
        Optional
                .ofNullable(testMethod.getAnnotation(annotationType))
                .ifPresent(onDetected);
    }

}
