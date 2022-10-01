package pl.netroute.hussar.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class PropertiesConfigurerTest {
    private PropertiesConfigurer configurer;

    @BeforeEach
    public void setup() {
        configurer = new PropertiesConfigurer();
    }

    @Test
    public void shouldSetProperties() {
        // given
        var propertyKeyA = "some-propertyA";
        var propertyValueA = "some-valueA";

        var propertyKeyB = "some-propertyB";
        var propertyValueB = "some-valueB";

        var propertiesConfig = new PropertiesConfiguration(
                Map.of(
                        propertyKeyA, propertyValueA,
                        propertyKeyB, propertyValueB
                )
        );

        // when
        configurer.configure(propertiesConfig);

        // then
        assertThat(System.getProperty(propertyKeyA)).isEqualTo(propertyValueA);
        assertThat(System.getProperty(propertyKeyB)).isEqualTo(propertyValueB);
    }

    @Test
    public void shouldOverrideAlreadySetProperties() {
        // given
        var propertyKey = "some-property";
        var propertyValue = "some-value";
        var propertyNewValue = "some-new-value";

        System.setProperty(propertyKey, propertyValue);

        var propertiesConfig = new PropertiesConfiguration(
                Map.of(
                        propertyKey, propertyNewValue
                )
        );

        // when
        configurer.configure(propertiesConfig);

        // then
        assertThat(System.getProperty(propertyKey)).isEqualTo(propertyNewValue);
    }

}
