package pl.netroute.hussar.core.service.api;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class RegistrableEnvVariableConfigurationEntryTest {

    @Test
    public void shouldCreateEntry() {
        // given
        var name = "SOME_ENV_VAR";

        // when
        var envVariableEntry = RegistrableConfigurationEntry.envVariable(name);

        // then
        assertCreatedEntry(name, envVariableEntry);
    }

    private void assertCreatedEntry(String expectedName, RegistrableConfigurationEntry entry) {
        assertThat(entry.getName()).isEqualTo(expectedName);
    }

}
