package pl.netroute.hussar.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class PropertiesCleanerTest {
    private PropertiesCleaner cleaner;

    @BeforeEach
    public void setup() {
        cleaner = new PropertiesCleaner();
    }

    @Test
    public void shouldCleanSetProperties() {
        // given
        var externalKeyA = "some-propertyA";
        var externalValueA = "some-valueA";
        System.setProperty(externalKeyA, externalValueA);

        var externalKeyB = "some-propertyB";
        var externalValueB = "some-valueB";
        System.setProperty(externalKeyB, externalValueB);

        var propertiesConfig = new PropertiesConfiguration(
                Map.of(
                        externalKeyA, externalValueA,
                        externalKeyB, externalValueB
                )
        );

        // when
        cleaner.clean(propertiesConfig);

        // then
        assertThat(System.getProperty(externalKeyA)).isNull();
        assertThat(System.getProperty(externalKeyB)).isNull();
    }

    @Test
    public void shouldSkipExternalProperties() {
        // given
        var propertiesConfig = new PropertiesConfiguration(Map.of());

        var externalKey = "some-property";
        var externalValue = "some-value";
        System.setProperty(externalKey, externalValue);

        // when
        cleaner.clean(propertiesConfig);

        // then
        assertThat(System.getProperty(externalKey)).isEqualTo(externalValue);
    }

}
