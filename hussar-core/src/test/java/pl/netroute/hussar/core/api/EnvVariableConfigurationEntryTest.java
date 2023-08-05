package pl.netroute.hussar.core.api;

import org.junit.jupiter.api.Test;

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
        assertThat(entry.getName()).isEqualTo(expectedName);
        assertThat(entry.getFormattedName()).isEqualTo(String.format(FORMATTED_NAME_TEMPLATE, expectedName));
        assertThat(entry.getValue()).isEqualTo(expectedValue);
    }
}
