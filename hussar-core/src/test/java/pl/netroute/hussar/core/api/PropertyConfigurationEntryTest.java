package pl.netroute.hussar.core.api;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PropertyConfigurationEntryTest {

    @Test
    public void shouldCreateEntry() {
        // given
        var name = "some.property";
        var value = "some-property-value";

        // when
        var propertyEntry = ConfigurationEntry.property(name, value);

        // then
        assertCreatedEntry(name, value, propertyEntry);
    }

    private void assertCreatedEntry(String expectedName, String expectedValue, PropertyConfigurationEntry entry) {
        assertThat(entry.getName()).isEqualTo(expectedName);
        assertThat(entry.getFormattedName()).isEqualTo(expectedName);
        assertThat(entry.getValue()).isEqualTo(expectedValue);
    }

}