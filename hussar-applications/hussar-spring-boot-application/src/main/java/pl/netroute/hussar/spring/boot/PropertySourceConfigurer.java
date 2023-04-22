package pl.netroute.hussar.spring.boot;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;

import java.util.Map;
import java.util.Objects;

class PropertySourceConfigurer {
    static final String PROPERTY_SOURCE_NAME = "hussar-spring-boot-application-property-source";

    void configure(Map<String, Object> properties, ConfigurableApplicationContext context) {
        Objects.requireNonNull(properties, "properties is required");
        Objects.requireNonNull(context, "context is required");

        var propertySources = context
                .getEnvironment()
                .getPropertySources();

        removeAllPropertySources(propertySources);
        configureMapPropertySource(properties, propertySources);
    }

    private void removeAllPropertySources(MutablePropertySources propertySources) {
        propertySources.forEach(source -> propertySources.remove(source.getName()));
    }

    private void configureMapPropertySource(Map<String, Object> properties, MutablePropertySources propertySources) {
        var source = new MapPropertySource(PROPERTY_SOURCE_NAME, properties);

        propertySources.addFirst(source);
    }

}
