package pl.netroute.hussar.spring.boot;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;

import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
class PropertySourceConfigurer {
    static final String PROPERTY_SOURCE_NAME = "hussar-spring-boot-application-property-source";

    void configure(@NonNull Map<String, Object> properties,
                   @NonNull ConfigurableApplicationContext context) {
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
