package pl.netroute.hussar.core.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MapConfigurationRegistryTest {
    private static final int SINGLE_CONFIG_ENTRY_REGISTERED = 1;

    private MapConfigurationRegistry registry;

    @BeforeEach
    public void setup() {
        registry = new MapConfigurationRegistry();
    }

    @Test
    public void shouldRegisterPropertyConfigEntry() {
        // given
        var propertyEntry = ConfigurationEntry.property("some.property", "some-property-value");

        // when
        registry.register(propertyEntry);

        // then
        assertRegisteredConfigEntries(SINGLE_CONFIG_ENTRY_REGISTERED);
        assertConfigEntryRegistered(propertyEntry);
    }

    @Test
    public void shouldRegisterEnvironmentVariableConfigEntry() {
        // given
        var envVariableEntry = ConfigurationEntry.envVariable("SOME_ENV_VAR", "some-env-var-value");

        // when
        registry.register(envVariableEntry);

        // then
        assertRegisteredConfigEntries(SINGLE_CONFIG_ENTRY_REGISTERED);
        assertConfigEntryRegistered(envVariableEntry);
    }

    @Test
    public void shouldSkipRegisteringDuplicatePropertyConfigEntries() {
        // given
        var propertyName = "some.property";
        var propertyEntry = ConfigurationEntry.property(propertyName, "some-property-value");
        var duplicatePropertyEntry = ConfigurationEntry.property(propertyName, "another-some-property-value");

        // when
        registry.register(propertyEntry);
        registry.register(duplicatePropertyEntry);

        // then
        assertRegisteredConfigEntries(SINGLE_CONFIG_ENTRY_REGISTERED);
        assertConfigEntryRegistered(propertyEntry);
    }

    @Test
    public void shouldSkipRegisteringDuplicateEnvironmentVariableConfigEntries() {
        // given
        var envVariableName = "SOME_ENV_VAR";
        var envVariableEntry = ConfigurationEntry.property(envVariableName, "some-env-var-value");
        var duplicateEnvVariableEntry = ConfigurationEntry.property(envVariableName, "another-some-env-var-value");

        // when
        registry.register(envVariableEntry);
        registry.register(duplicateEnvVariableEntry);

        // then
        assertRegisteredConfigEntries(SINGLE_CONFIG_ENTRY_REGISTERED);
        assertConfigEntryRegistered(envVariableEntry);
    }

    private void assertRegisteredConfigEntries(int numberOfRegisteredEntries) {
        assertThat(registry.getEntries()).hasSize(numberOfRegisteredEntries);
    }

    private void assertConfigEntryRegistered(ConfigurationEntry expectedEntry) {
        var registeredEntry = registry
                .getEntries()
                .stream()
                .filter(actualEntry -> actualEntry.equals(expectedEntry))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Expected config entry to be registered"));

        assertThat(registeredEntry.getName()).isEqualTo(expectedEntry.getName());
        assertThat(registeredEntry.getFormattedName()).isEqualTo(expectedEntry.getFormattedName());
        assertThat(registeredEntry.getValue()).isEqualTo(expectedEntry.getValue());
    }

}
