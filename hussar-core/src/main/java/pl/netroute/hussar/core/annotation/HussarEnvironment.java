package pl.netroute.hussar.core.annotation;

import pl.netroute.hussar.core.api.EnvironmentConfigurerProvider;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface HussarEnvironment {
    Class<? extends EnvironmentConfigurerProvider> configurerProvider();
}
