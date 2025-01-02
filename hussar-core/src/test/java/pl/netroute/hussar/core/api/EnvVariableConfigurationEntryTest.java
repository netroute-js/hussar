package pl.netroute.hussar.core.api;

import org.junit.jupiter.api.Test;
import pl.netroute.hussar.core.configuration.api.ConfigurationEntry;
import pl.netroute.hussar.core.configuration.api.EnvVariableConfigurationEntry;

import static org.assertj.core.api.Assertions.assertThat;

public class EnvVariableConfigurationEntryTest {
    private static final String FORMATTED_NAME_TEMPLATE = "${%s}";

    @Test
    public void shouldCreateEntry() {
        // given
        var name = "SOME_ENV_VAR";
        var value = "some-env-var-value";

        // when
        var envVariableEntry = ConfigurationEntry.envVariable(name, value);

        // then
        assertCreatedEntry(name, value, envVariableEntry);
    }

    private void assertCreatedEntry(String expectedName, String expectedValue, EnvVariableConfigurationEntry entry) {
        assertThat(entry.name()).isEqualTo(expectedName);
        assertThat(entry.formattedName()).isEqualTo(String.format(FORMATTED_NAME_TEMPLATE, expectedName));
        assertThat(entry.value()).isEqualTo(expectedValue);
    }
}
