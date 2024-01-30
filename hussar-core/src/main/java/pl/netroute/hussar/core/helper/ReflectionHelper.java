package pl.netroute.hussar.core.helper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReflectionHelper {

    public static <T> T newInstance(@NonNull Class<T> type) {
        Objects.requireNonNull(type, "type is required");

        try {
            var constructor = type.getConstructor();
            constructor.setAccessible(true);

            return constructor.newInstance();
        } catch (Exception ex) {
            throw new IllegalStateException("Could not create new instance of " + type, ex);
        }
    }

    public static <A extends Annotation> List<Field> getFieldsAnnotatedWith(@NonNull Object target,
                                                                            @NonNull Class<A> annotationType) {
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

    public static void setValue(@NonNull Object target,
                                @NonNull Field field,
                                @NonNull Object value) {
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
