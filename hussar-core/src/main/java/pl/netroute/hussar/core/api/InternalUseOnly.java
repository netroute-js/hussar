package pl.netroute.hussar.core.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation marks classes as Hussar internal types.
 * They should not be used outside of Hussar. There is a high chance that they can/will change.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface InternalUseOnly {
}
