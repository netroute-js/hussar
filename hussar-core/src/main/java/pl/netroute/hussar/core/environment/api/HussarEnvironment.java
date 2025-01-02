package pl.netroute.hussar.core.environment.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Hussar annotation responsible for tagging and thus providing a testing environment class.
 */
@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface HussarEnvironment {

    /**
     * Returns a class that implements {@link EnvironmentConfigurerProvider}.
     * This class will be used by Hussar to provide testing environment on runtime.
     *
     * @return the class implementing {@link EnvironmentConfigurerProvider}.
     */
    Class<? extends EnvironmentConfigurerProvider> configurerProvider();

}
