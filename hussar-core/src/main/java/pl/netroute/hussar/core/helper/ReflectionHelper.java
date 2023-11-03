package pl.netroute.hussar.core.helper;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ReflectionHelper {

    private ReflectionHelper() {
    }

    public static <T> T newInstance(Class<T> type) {
        Objects.requireNonNull(type, "type is required");

        try {
            var constructor = type.getConstructor();
            constructor.setAccessible(true);

            return constructor.newInstance();
        } catch (Exception ex) {
            throw new IllegalStateException("Could not create new instance of " + type, ex);
        }
    }

    public static <A extends Annotation> List<Field> getFieldsAnnotatedWith(Object target, Class<A> annotationType) {
        Objects.requireNonNull(target, "target is required");
        Objects.requireNonNull(annotationType, "annotationType is required");

        var fields = target
                .getClass()
                .getDeclaredFields();

        return Stream
                .of(fields)
                .filter(field -> field.isAnnotationPresent(annotationType))
                .collect(Collectors.toUnmodifiableList());
    }

    public static void setValue(Object target, Field field, Object value) {
        Objects.requireNonNull(target, "target is required");
        Objects.requireNonNull(field, "field is required");

        try {
            field.setAccessible(true);
            field.set(target, value);
        } catch(IllegalAccessException ex) {
            throw new IllegalStateException("Unable to set value", ex);
        }
    }

}
