package pl.netroute.hussar.core.service.api;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class RegistrablePropertyConfigurationEntryTest {

    @Test
    public void shouldCreateEntry() {
        // given
        var name = "some.property";

        // when
        var propertyEntry = RegistrableConfigurationEntry.property(name);

        // then
        assertCreatedEntry(name, propertyEntry);
    }

    private void assertCreatedEntry(String expectedName, RegistrableConfigurationEntry entry) {
        assertThat(entry.getName()).isEqualTo(expectedName);
    }

}
