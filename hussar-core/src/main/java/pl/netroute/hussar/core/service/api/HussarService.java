package pl.netroute.hussar.core.service.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Hussar annotation responsible for injecting the {@link Service} into test instance.
 */
@Inherited
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface HussarService {

    /**
     * Returns {@link Service} name.
     * It's crucial to specify name if there are more {@link Service} of the same type.
     * In such cases, Hussar will use the name to do actual injection.
     *
     * @return the name of {@link Service}
     */
    String name() default "";

}
