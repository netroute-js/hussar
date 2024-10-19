package pl.netroute.hussar.spring.boot;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.netroute.hussar.core.api.configuration.ConfigurationEntry;

import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.netroute.hussar.core.api.configuration.ConfigurationEntry.envVariable;
import static pl.netroute.hussar.core.api.configuration.ConfigurationEntry.property;

public class ConfigurationResolverTest {
    private static final String SERVER_NAME_PROPERTY = "server.name";
    private static final String SERVER_NAME_PROPERTY_VALUE = "hussar-application";

    private static final String SERVER_URL_PROPERTY = "server.url";
    private static final String SERVER_URL_1_PROPERTY = "server.url[0]";
    private static final String SERVER_URL_2_PROPERTY = "server.url[1]";
    private static final String SERVER_URL_1_PROPERTY_VALUE = "https://eu-hussar.dev";
    private static final String SERVER_URL_2_PROPERTY_VALUE = "https://us-hussar.dev";

    private static final String METRICS_URL_PROPERTY = "metrics.url";
    private static final String METRICS_URL_ENV_VARIABLE = "METRICS_URL";
    private static final String METRICS_URL_ENV_VARIABLE_VALUE = "https://hussar.dev/metrics";

    private ConfigurationResolver resolver;

    @BeforeEach
    public void setup() {
        resolver = new ConfigurationResolver();
    }

    @Test
    public void shouldResolvePropertyConfiguration() {
        // given
        var property = property(SERVER_NAME_PROPERTY, SERVER_NAME_PROPERTY_VALUE);
        var externalConfigurations = Set.<ConfigurationEntry>of(property);

        var applicationConfigurations = Map.<String, Object>of(
                SERVER_URL_1_PROPERTY, SERVER_URL_1_PROPERTY_VALUE,
                SERVER_URL_2_PROPERTY, SERVER_URL_2_PROPERTY_VALUE
        );

        // when
        var resolvedConfigurations = resolver.resolve(applicationConfigurations, externalConfigurations);

        // then
        var expectedConfigurations = Map.<String, Object>of(
                SERVER_NAME_PROPERTY, SERVER_NAME_PROPERTY_VALUE,
                SERVER_URL_1_PROPERTY, SERVER_URL_1_PROPERTY_VALUE,
                SERVER_URL_2_PROPERTY, SERVER_URL_2_PROPERTY_VALUE
        );

        assertResolvedConfiguration(resolvedConfigurations, expectedConfigurations);
    }

    @Test
    public void shouldResolveEnvironmentVariableConfiguration() {
        // given
        var environmentVariable = envVariable(METRICS_URL_ENV_VARIABLE, METRICS_URL_ENV_VARIABLE_VALUE);
        var externalConfigurations = Set.<ConfigurationEntry>of(environmentVariable);

        var applicationConfigurations = Map.<String, Object>of(
                SERVER_NAME_PROPERTY, SERVER_NAME_PROPERTY_VALUE,
                METRICS_URL_PROPERTY, environmentVariable.formattedName()
        );

        // when
        var resolvedConfigurations = resolver.resolve(applicationConfigurations, externalConfigurations);

        // then
        var expectedConfigurations = Map.<String, Object>of(
                SERVER_NAME_PROPERTY, SERVER_NAME_PROPERTY_VALUE,
                METRICS_URL_PROPERTY, METRICS_URL_ENV_VARIABLE_VALUE
        );

        assertResolvedConfiguration(resolvedConfigurations, expectedConfigurations);
    }

    @Test
    public void shouldIgnoreEnvironmentVariableConfiguration() {
        // given
        var environmentVariable = envVariable(METRICS_URL_ENV_VARIABLE, METRICS_URL_ENV_VARIABLE_VALUE);
        var externalConfigurations = Set.<ConfigurationEntry>of(environmentVariable);

        var applicationConfigurations = Map.<String, Object>of(
                SERVER_NAME_PROPERTY, SERVER_NAME_PROPERTY_VALUE
        );

        // when
        var resolvedConfigurations = resolver.resolve(applicationConfigurations, externalConfigurations);

        // then
        var expectedConfigurations = Map.<String, Object>of(
                SERVER_NAME_PROPERTY, SERVER_NAME_PROPERTY_VALUE
        );

        assertResolvedConfiguration(resolvedConfigurations, expectedConfigurations);
    }

    @Test
    public void shouldOverwritePropertyConfiguration() {
        // given
        var serverNameValue = "hussar-application-overwrite";
        var overwriteProperty = property(SERVER_NAME_PROPERTY, serverNameValue);
        var externalConfigurations = Set.<ConfigurationEntry>of(overwriteProperty);

        var applicationConfigurations = Map.<String, Object>of(
                SERVER_NAME_PROPERTY, SERVER_NAME_PROPERTY_VALUE
        );

        // when
        var resolvedConfigurations = resolver.resolve(applicationConfigurations, externalConfigurations);

        // then
        var expectedConfigurations = Map.<String, Object>of(
                SERVER_NAME_PROPERTY, serverNameValue
        );

        assertResolvedConfiguration(resolvedConfigurations, expectedConfigurations);
    }

    @Test
    public void shouldOverwriteMultiValuePropertyConfiguration() {
        // given
        var serverUrlValue = "https://hussar.dev";
        var overwriteProperty = property(SERVER_URL_PROPERTY, serverUrlValue);
        var externalConfigurations = Set.<ConfigurationEntry>of(overwriteProperty);

        var applicationConfigurations = Map.<String, Object>of(
                SERVER_URL_1_PROPERTY, SERVER_URL_1_PROPERTY_VALUE,
                SERVER_URL_2_PROPERTY, SERVER_URL_2_PROPERTY_VALUE
        );

        // when
        var resolvedConfigurations = resolver.resolve(applicationConfigurations, externalConfigurations);

        // then
        var expectedConfigurations = Map.<String, Object>of(
                SERVER_URL_PROPERTY, serverUrlValue
        );

        assertResolvedConfiguration(resolvedConfigurations, expectedConfigurations);
    }

    private void assertResolvedConfiguration(Map<String, Object> actualConfigurations, Map<String, Object> expectedConfigurations) {
        assertThat(actualConfigurations).isEqualTo(expectedConfigurations);
    }
//
//    @Test
//    public void shouldResolveSimpleYamlConfiguration() {
//        // given
//        var configurationFile = resolveConfigurationFileFromClasspath(SIMPLE_YML_CONFIGURATION_FILE_PATH);
//        var application = new TestApplication(configurationFile);
//
//        // when
//        var applicationConfiguration = resolver.resolve(application, List.of());
//
//        // then
//        var expectedApplicationConfiguration = Map.<String, Object>of(
//                CONFIGURATION_A, CONFIGURATION_A_VALUE,
//                CONFIGURATION_D, CONFIGURATION_D_VALUE,
//                CONFIGURATION_B, CONFIGURATION_B_VALUE,
//                CONFIGURATION_C_0, CONFIGURATION_C_0_VALUE,
//                CONFIGURATION_C_1, CONFIGURATION_C_1_VALUE,
//                CONFIGURATION_C_2, CONFIGURATION_C_2_VALUE,
//                CONFIGURATION_E_KEY_1, CONFIGURATION_E_KEY1_VALUE,
//                CONFIGURATION_E_KEY_2, CONFIGURATION_E_KEY2_VALUE,
//                CONFIGURATION_E_KEY_3, CONFIGURATION_E_KEY3_VALUE
//        );
//
//        assertResolvedApplicationConfiguration(applicationConfiguration, expectedApplicationConfiguration);
//    }
//
//    @Test
//    public void shouldResolveExternalizedYamlConfiguration() {
//        // given
//        var configurationFile = resolveConfigurationFileFromClasspath(EXTERNALIZED_YML_CONFIGURATION_FILE_PATH);
//        var application = new TestApplication(configurationFile);
//
//        var externalConfigurationRegistryA = new MapConfigurationRegistry(
//                Set.of(
//                        ConfigurationEntry.envVariable(ENV_VARIABLE_C, ENV_VARIABLE_C_VALUE),
//                        ConfigurationEntry.envVariable(ENV_VARIABLE_E, ENV_VARIABLE_E_VALUE)
//                )
//        );
//
//        var externalConfigurationRegistryB = new MapConfigurationRegistry(
//                Set.of(
//                        ConfigurationEntry.envVariable(ENV_VARIABLE_D, ENV_VARIABLE_D_VALUE),
//                        ConfigurationEntry.property(CONFIGURATION_G, CONFIGURATION_G_OVERRIDDEN_VALUE)
//                )
//        );
//
//        var externalConfigurationRegistryC = new MapConfigurationRegistry(
//                Set.of(
//                        ConfigurationEntry.envVariable(ENV_VARIABLE_F, ENV_VARIABLE_F_VALUE),
//                        ConfigurationEntry.property(CONFIGURATION_H, CONFIGURATION_H_OVERRIDDEN_VALUE)
//                )
//        );
//
//        // when
//        var applicationConfiguration = resolver.resolve(
//                application,
//                List.of(
//                        externalConfigurationRegistryA,
//                        externalConfigurationRegistryB,
//                        externalConfigurationRegistryC
//                )
//        );
//
//        // then
//        var expectedApplicationConfiguration = new HashMap<String, Object>();
//        expectedApplicationConfiguration.put(CONFIGURATION_A, CONFIGURATION_A_VALUE);
//        expectedApplicationConfiguration.put(CONFIGURATION_D, ENV_VARIABLE_D_VALUE);
//        expectedApplicationConfiguration.put(CONFIGURATION_B, CONFIGURATION_B_VALUE);
//        expectedApplicationConfiguration.put(CONFIGURATION_C_0, CONFIGURATION_C_0_VALUE);
//        expectedApplicationConfiguration.put(CONFIGURATION_C_1, CONFIGURATION_C_1_VALUE);
//        expectedApplicationConfiguration.put(CONFIGURATION_C_2, ENV_VARIABLE_C_VALUE);
//        expectedApplicationConfiguration.put(CONFIGURATION_E_KEY_1, CONFIGURATION_E_KEY1_VALUE);
//        expectedApplicationConfiguration.put(CONFIGURATION_E_KEY_2, ENV_VARIABLE_E_VALUE);
//        expectedApplicationConfiguration.put(CONFIGURATION_E_KEY_3, CONFIGURATION_E_KEY3_VALUE);
//        expectedApplicationConfiguration.put(CONFIGURATION_F, ENV_VARIABLE_F_VALUE);
//        expectedApplicationConfiguration.put(CONFIGURATION_H, CONFIGURATION_H_OVERRIDDEN_VALUE);
//        expectedApplicationConfiguration.put(CONFIGURATION_G, CONFIGURATION_G_OVERRIDDEN_VALUE);
//
//        assertResolvedApplicationConfiguration(applicationConfiguration, expectedApplicationConfiguration);
//    }
//
//    @Test
//    public void shouldResolveSimplePropertiesConfiguration() {
//        // given
//        var configurationFile = resolveConfigurationFileFromClasspath(SIMPLE_PROPERTIES_CONFIGURATION_FILE_PATH);
//        var application = new TestApplication(configurationFile);
//
//        // when
//        var applicationConfiguration = resolver.resolve(application, List.of());
//
//        // then
//        var expectedApplicationConfiguration = Map.<String, Object>of(
//                CONFIGURATION_A, CONFIGURATION_A_VALUE,
//                CONFIGURATION_B, CONFIGURATION_B_VALUE,
//                CONFIGURATION_D, CONFIGURATION_D_VALUE
//        );
//
//        assertResolvedApplicationConfiguration(applicationConfiguration, expectedApplicationConfiguration);
//    }
//
//    @Test
//    public void shouldResolveExternalizedPropertiesConfiguration() {
//        // given
//        var configurationFile = resolveConfigurationFileFromClasspath(EXTERNALIZED_PROPERTIES_CONFIGURATION_FILE_PATH);
//        var application = new TestApplication(configurationFile);
//
//        var externalConfigurationRegistryA = new MapConfigurationRegistry(
//                Set.of(
//                        ConfigurationEntry.envVariable(ENV_VARIABLE_D, ENV_VARIABLE_D_VALUE)
//                )
//        );
//
//        var externalConfigurationRegistryB = new MapConfigurationRegistry(
//                Set.of(
//                        ConfigurationEntry.property(CONFIGURATION_G, CONFIGURATION_G_OVERRIDDEN_VALUE)
//                )
//        );
//
//        // when
//        var applicationConfiguration = resolver.resolve(
//                application,
//                List.of(
//                        externalConfigurationRegistryA,
//                        externalConfigurationRegistryB
//                )
//        );
//
//        // then
//        var expectedApplicationConfiguration = new HashMap<String, Object>();
//        expectedApplicationConfiguration.put(CONFIGURATION_A, CONFIGURATION_A_VALUE);
//        expectedApplicationConfiguration.put(CONFIGURATION_D, ENV_VARIABLE_D_VALUE);
//        expectedApplicationConfiguration.put(CONFIGURATION_G, CONFIGURATION_G_OVERRIDDEN_VALUE);
//
//        assertResolvedApplicationConfiguration(applicationConfiguration, expectedApplicationConfiguration);
//    }
//
//    private void assertResolvedApplicationConfiguration(Map<String, Object> applicationConfiguration, Map<String, Object> expectedApplicationConfiguration) {
//        assertThat(applicationConfiguration).containsExactlyInAnyOrderEntriesOf(expectedApplicationConfiguration);
//    }

}
