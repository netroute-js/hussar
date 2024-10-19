package pl.netroute.hussar.core.helper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * A simple Reflection helper.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReflectionHelper {

    /**
     * Creates a new instance of the given type by invoking default constructor.
     *
     * @param <T> - the type of the object to create.
     * @param type - the type of the object to create.
     * @return an instance of created object.
     */
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

    /**
     * Returns fields annotated with the given annotation.
     *
     * @param <A> - type of annotation.
     * @param target - a target object on which type the lookup shall be performed.
     * @param annotationType - type of annotation
     * @return list of fields annotated with the given annotation.
     */
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
                .toList();
    }

    /**
     * Sets a value of a given {@link Field} in a given object.
     *
     * @param target - the parent object of the {@link Field}.
     * @param field - the {@link Field} for which the value shall be set.
     * @param value - the value to set in a given {@link Field}.
     */
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
