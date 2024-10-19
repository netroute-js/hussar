package pl.netroute.hussar.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Hussar annotation responsible for injecting the {@link pl.netroute.hussar.core.api.Service} into test instance.
 */
@Inherited
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface HussarService {

    /**
     * Returns {@link pl.netroute.hussar.core.api.Service} name.
     * It's crucial to specify name if there are more {@link pl.netroute.hussar.core.api.Service} of the same type.
     * In such cases, Hussar will use the name to do actual injection.
     *
     * @return the name of {@link pl.netroute.hussar.core.api.Service}
     */
    String name() default "";

}
