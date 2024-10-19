package pl.netroute.hussar.spring.boot;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import pl.netroute.hussar.core.api.configuration.ConfigurationEntry;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class PropertySourceConfigurer {
    private static final Set<String> IGNORE_MAP_PROPERTY_SOURCES = Set.of("systemProperties", "systemEnvironment");

    private final ConfigurationResolver configurationResolver;

    void configure(@NonNull Set<ConfigurationEntry> externalConfigurations,
                   @NonNull ConfigurableApplicationContext context) {
        var mutablePropertySources = context
                .getEnvironment()
                .getPropertySources();

        var enhancedPropertySources = mutablePropertySources
                .stream()
                .map(propertySource -> enhancePropertySource(propertySource, externalConfigurations))
                .toList();

        removeAllPropertySources(mutablePropertySources);
        configurePropertySource(enhancedPropertySources, mutablePropertySources);
    }

    private PropertySource<?> enhancePropertySource(PropertySource<?> propertySource,
                                                    Set<ConfigurationEntry> externalConfigurations) {
        if(propertySource instanceof MapPropertySource mapPropertySource && shallEnhancePropertySource(propertySource)) {
            return enhanceMapPropertySource(mapPropertySource, externalConfigurations);
        }

        return propertySource;
    }

    private PropertySource<?> enhanceMapPropertySource(MapPropertySource mapPropertySource,
                                                       Set<ConfigurationEntry> externalConfigurations) {
        var name = mapPropertySource.getName();
        var propertiesMap = mapPropertySource.getSource();
        var reconfiguredPropertiesMap = configurationResolver.resolve(propertiesMap, externalConfigurations);

        return new MapPropertySource(name, reconfiguredPropertiesMap);
    }

    private boolean shallEnhancePropertySource(PropertySource<?> propertySource) {
        var name = propertySource.getName();

        return !IGNORE_MAP_PROPERTY_SOURCES.contains(name);
    }

    private void removeAllPropertySources(MutablePropertySources propertySources) {
        propertySources.forEach(source -> propertySources.remove(source.getName()));
    }

    private void configurePropertySource(List<? extends PropertySource<?>> propertySources, MutablePropertySources mutablePropertySources) {
        propertySources.forEach(mutablePropertySources::addLast);
    }

}
