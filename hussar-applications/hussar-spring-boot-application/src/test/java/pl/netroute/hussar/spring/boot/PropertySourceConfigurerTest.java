package pl.netroute.hussar.spring.boot;

import org.assertj.core.internal.bytebuddy.utility.RandomString;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.StandardEnvironment;
import pl.netroute.hussar.core.api.configuration.ConfigurationEntry;
import pl.netroute.hussar.core.api.configuration.PropertyConfigurationEntry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

public class PropertySourceConfigurerTest {
    private static final String SYSTEM_PROPERTIES_MAP_PROPERTY_SOURCE = "systemProperties";
    private static final String SYSTEM_ENVIRONMENT_MAP_PROPERTY_SOURCE = "systemEnvironment";

    private static final String SERVER_NAME_PROPERTY = "server.name";
    private static final String SERVER_NAME_PROPERTY_VALUE = "hussar-application";

    private PropertySourceConfigurer propertySourceConfigurer;

    @BeforeEach
    public void setup() {
        var configurationResolver = new ConfigurationResolver();

        propertySourceConfigurer = new PropertySourceConfigurer(configurationResolver);
    }

    @Test
    public void shouldReconfigureMapPropertySources() {
        // given
        var externalConfigurations = Set.<ConfigurationEntry>of(
                ConfigurationEntry.property(SERVER_NAME_PROPERTY, SERVER_NAME_PROPERTY_VALUE)
        );

        var mapSourceName = "hussar-map-source";
        var mapSource = generateRandomSource();
        var mapPropertySource = createMapPropertySource(mapSourceName, mapSource);
        var propertySources = List.<PropertySource<?>>of(mapPropertySource);
        var applicationContext = createApplicationContext(propertySources);

        // when
        propertySourceConfigurer.configure(externalConfigurations, applicationContext);

        // then
        var expectedMapPropertySource = createMapPropertySource(mapPropertySource, externalConfigurations);
        var expectedPropertySources = List.of(expectedMapPropertySource);

        assertPropertySourcesConfigured(applicationContext, expectedPropertySources);
    }

    @Test
    public void shouldSkipReconfiguringSystemProperties() {
        // given
        var externalConfigurations = Set.<ConfigurationEntry>of(
                ConfigurationEntry.property(SERVER_NAME_PROPERTY, SERVER_NAME_PROPERTY_VALUE)
        );

        var mapSource = generateRandomSource();
        var mapPropertySource = createMapPropertySource(SYSTEM_PROPERTIES_MAP_PROPERTY_SOURCE, mapSource);
        var propertySources = List.<PropertySource<?>>of(mapPropertySource);
        var applicationContext = createApplicationContext(propertySources);

        // when
        propertySourceConfigurer.configure(externalConfigurations, applicationContext);

        // then
        assertPropertySourcesConfigured(applicationContext, propertySources);
    }

    @Test
    public void shouldSkipReconfiguringSystemEnvironmentVariables() {
        // given
        var externalConfigurations = Set.<ConfigurationEntry>of(
                ConfigurationEntry.property(SERVER_NAME_PROPERTY, SERVER_NAME_PROPERTY_VALUE)
        );

        var mapSource = generateRandomSource();
        var mapPropertySource = createMapPropertySource(SYSTEM_ENVIRONMENT_MAP_PROPERTY_SOURCE, mapSource);
        var propertySources = List.<PropertySource<?>>of(mapPropertySource);
        var applicationContext = createApplicationContext(propertySources);

        // when
        propertySourceConfigurer.configure(externalConfigurations, applicationContext);

        // then
        assertPropertySourcesConfigured(applicationContext, propertySources);
    }

    private ConfigurableApplicationContext createApplicationContext(List<? extends PropertySource<?>> propertySources) {
        var environment = new StandardEnvironment();
        var mutablePropertySources = environment.getPropertySources();

        mutablePropertySources.forEach(propertySource -> mutablePropertySources.remove(propertySource.getName()));
        propertySources.forEach(mutablePropertySources::addLast);

        var context = new StaticApplicationContext();
        context.setEnvironment(environment);

        return context;
    }

    private MapPropertySource createMapPropertySource(MapPropertySource propertySource, Set<ConfigurationEntry> externalConfigurations) {
        var name = propertySource.getName();
        var sourceMap = new HashMap<>(propertySource.getSource());

        externalConfigurations
                .stream()
                .filter(configurationEntry -> configurationEntry instanceof PropertyConfigurationEntry)
                .forEach(configurationEntry -> sourceMap.put(configurationEntry.formattedName(), configurationEntry.value()));

        return createMapPropertySource(name, sourceMap);
    }

    private MapPropertySource createMapPropertySource(String name, Map<String, Object> source) {
        return new MapPropertySource(name, source);
    }

    private Map<String, Object> generateRandomSource() {
        return Map.of(
                RandomString.make(), RandomString.make(),
                RandomString.make(), RandomString.make()
        );
    }

    private void assertPropertySourcesConfigured(ConfigurableApplicationContext applicationContext, List<? extends PropertySource<?>> propertySources) {
        var actualPropertySources = applicationContext
                .getEnvironment()
                .getPropertySources()
                .stream()
                .toList();

        assertThat(actualPropertySources).hasSize(propertySources.size());

        IntStream
                .range(0, actualPropertySources.size())
                .forEach(index -> assertPropertySources(actualPropertySources.get(index), propertySources.get(index)));
    }

    private void assertPropertySources(PropertySource<?> actualPropertySource, PropertySource<?> expectedPropertySource) {
        assertThat(actualPropertySource.getName()).isEqualTo(expectedPropertySource.getName());
        assertThat(actualPropertySource.getSource()).isEqualTo(expectedPropertySource.getSource());
    }

}
