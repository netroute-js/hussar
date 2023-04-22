package pl.netroute.hussar.spring.boot;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.StandardEnvironment;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class PropertySourceConfigurerTest {
    private PropertySourceConfigurer propertySourceConfigurer;

    @BeforeEach
    public void setup() {
        propertySourceConfigurer = new PropertySourceConfigurer();
    }

    @Test
    public void shouldConfigureSinglePropertySource() {
        // given
        var properties = Map.<String, Object>of("propertyA", "valueA");

        var propertySourceA = new MapPropertySource("sourceA", Map.of());
        var propertySourceB = new MapPropertySource("sourceB", Map.of());
        var applicationContext = prepareApplicationContext(List.of(propertySourceA, propertySourceB));

        // when
        propertySourceConfigurer.configure(properties, applicationContext);

        // then
        assertSinglePropertySourceConfigured(applicationContext, properties);
    }

    private ConfigurableApplicationContext prepareApplicationContext(List<PropertySource> propertySources) {
        var environment = new StandardEnvironment();
        propertySources.forEach(propertySource -> environment.getPropertySources().addFirst(propertySource));

        var context = new StaticApplicationContext();
        context.setEnvironment(environment);

        return context;
    }

    private void assertSinglePropertySourceConfigured(ConfigurableApplicationContext applicationContext, Map<String, Object> expectedProperties) {
        var expectedPropertySource = new MapPropertySource(PropertySourceConfigurer.PROPERTY_SOURCE_NAME, expectedProperties);

        assertThat(applicationContext.getEnvironment().getPropertySources())
                .containsExactly(expectedPropertySource);
    }
}
