package pl.netroute.hussar.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.netroute.hussar.core.api.ConfigurationEntry;
import pl.netroute.hussar.core.api.MapConfigurationRegistry;
import pl.netroute.hussar.core.domain.TestApplication;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class ApplicationConfigurationResolverTest {
    private static final String SIMPLE_YML_CONFIGURATION_FILE_PATH = "application.yml";
    private static final String EXTERNALIZED_YML_CONFIGURATION_FILE_PATH = "application-externalized.yml";

    private static final String SIMPLE_PROPERTIES_CONFIGURATION_FILE_PATH = "application.properties";
    private static final String EXTERNALIZED_PROPERTIES_CONFIGURATION_FILE_PATH = "application-externalized.properties";

    private static final String CONFIGURATION_A = "some.propertyA";
    private static final String CONFIGURATION_B = "another.propertyB";
    private static final String CONFIGURATION_C_0 = "another.propertyC[0]";
    private static final String CONFIGURATION_C_1 = "another.propertyC[1]";
    private static final String CONFIGURATION_C_2 = "another.propertyC[2]";
    private static final String CONFIGURATION_E_KEY_1 = "another.propertyE.key1";
    private static final String CONFIGURATION_E_KEY_2 = "another.propertyE.key2";
    private static final String CONFIGURATION_E_KEY_3 = "another.propertyE.key3";
    private static final String CONFIGURATION_F = "some.propertyF";
    private static final String CONFIGURATION_D = "propertyD";
    private static final String CONFIGURATION_H = "another.propertyH";
    private static final String CONFIGURATION_G = "propertyG";

    private static final String ENV_VARIABLE_C = "ENV_VARIABLE_C";
    private static final String ENV_VARIABLE_D = "ENV_VARIABLE_D";
    private static final String ENV_VARIABLE_E = "ENV_VARIABLE_E";
    private static final String ENV_VARIABLE_F = "ENV_VARIABLE_F";

    private static final String CONFIGURATION_A_VALUE = "some-valueA";
    private static final String CONFIGURATION_B_VALUE = "some-valueB";
    private static final String CONFIGURATION_D_VALUE = "some-valueD";
    private static final String CONFIGURATION_C_0_VALUE = "some-valueC1";
    private static final String CONFIGURATION_C_1_VALUE = "some-valueC2";
    private static final String CONFIGURATION_C_2_VALUE = "some-valueC3";
    private static final int CONFIGURATION_E_KEY1_VALUE = 1;
    private static final int CONFIGURATION_E_KEY2_VALUE = 2;
    private static final int CONFIGURATION_E_KEY3_VALUE = 3;

    private static final String CONFIGURATION_H_OVERRIDDEN_VALUE = "some-value-overriddenH";
    private static final String CONFIGURATION_G_OVERRIDDEN_VALUE = "some-value-overriddenG";

    private static final String ENV_VARIABLE_C_VALUE = "env-variable-c-value";
    private static final String ENV_VARIABLE_D_VALUE = "env-variable-d-value";
    private static final String ENV_VARIABLE_E_VALUE = "env-variable-e-value";
    private static final String ENV_VARIABLE_F_VALUE = "env-variable-f-value";

    private ApplicationConfigurationResolver resolver;

    @BeforeEach
    public void setup() {
        var applicationConfigurationFlattener = new ApplicationConfigurationFlattener();
        var applicationConfigurationLoader = new ApplicationConfigurationLoader(applicationConfigurationFlattener);

        resolver = new ApplicationConfigurationResolver(applicationConfigurationLoader, applicationConfigurationFlattener);
    }

    @Test
    public void shouldResolveSimpleYamlConfiguration() {
        // given
        var configurationFile = resolveConfigurationFileFromClasspath(SIMPLE_YML_CONFIGURATION_FILE_PATH);
        var application = new TestApplication(configurationFile);

        // when
        var applicationConfiguration = resolver.resolve(application, List.of());

        // then
        var expectedApplicationConfiguration = Map.<String, Object>of(
                CONFIGURATION_A, CONFIGURATION_A_VALUE,
                CONFIGURATION_D, CONFIGURATION_D_VALUE,
                CONFIGURATION_B, CONFIGURATION_B_VALUE,
                CONFIGURATION_C_0, CONFIGURATION_C_0_VALUE,
                CONFIGURATION_C_1, CONFIGURATION_C_1_VALUE,
                CONFIGURATION_C_2, CONFIGURATION_C_2_VALUE,
                CONFIGURATION_E_KEY_1, CONFIGURATION_E_KEY1_VALUE,
                CONFIGURATION_E_KEY_2, CONFIGURATION_E_KEY2_VALUE,
                CONFIGURATION_E_KEY_3, CONFIGURATION_E_KEY3_VALUE
        );

        assertResolvedApplicationConfiguration(applicationConfiguration, expectedApplicationConfiguration);
    }

    @Test
    public void shouldResolveExternalizedYamlConfiguration() {
        // given
        var configurationFile = resolveConfigurationFileFromClasspath(EXTERNALIZED_YML_CONFIGURATION_FILE_PATH);
        var application = new TestApplication(configurationFile);

        var externalConfigurationRegistryA = new MapConfigurationRegistry(
                Set.of(
                        ConfigurationEntry.envVariable(ENV_VARIABLE_C, ENV_VARIABLE_C_VALUE),
                        ConfigurationEntry.envVariable(ENV_VARIABLE_E, ENV_VARIABLE_E_VALUE)
                )
        );

        var externalConfigurationRegistryB = new MapConfigurationRegistry(
                Set.of(
                        ConfigurationEntry.envVariable(ENV_VARIABLE_D, ENV_VARIABLE_D_VALUE),
                        ConfigurationEntry.property(CONFIGURATION_G, CONFIGURATION_G_OVERRIDDEN_VALUE)
                )
        );

        var externalConfigurationRegistryC = new MapConfigurationRegistry(
                Set.of(
                        ConfigurationEntry.envVariable(ENV_VARIABLE_F, ENV_VARIABLE_F_VALUE),
                        ConfigurationEntry.property(CONFIGURATION_H, CONFIGURATION_H_OVERRIDDEN_VALUE)
                )
        );

        // when
        var applicationConfiguration = resolver.resolve(
                application,
                List.of(
                        externalConfigurationRegistryA,
                        externalConfigurationRegistryB,
                        externalConfigurationRegistryC
                )
        );

        // then
        var expectedApplicationConfiguration = new HashMap<String, Object>();
        expectedApplicationConfiguration.put(CONFIGURATION_A, CONFIGURATION_A_VALUE);
        expectedApplicationConfiguration.put(CONFIGURATION_D, ENV_VARIABLE_D_VALUE);
        expectedApplicationConfiguration.put(CONFIGURATION_B, CONFIGURATION_B_VALUE);
        expectedApplicationConfiguration.put(CONFIGURATION_C_0, CONFIGURATION_C_0_VALUE);
        expectedApplicationConfiguration.put(CONFIGURATION_C_1, CONFIGURATION_C_1_VALUE);
        expectedApplicationConfiguration.put(CONFIGURATION_C_2, ENV_VARIABLE_C_VALUE);
        expectedApplicationConfiguration.put(CONFIGURATION_E_KEY_1, CONFIGURATION_E_KEY1_VALUE);
        expectedApplicationConfiguration.put(CONFIGURATION_E_KEY_2, ENV_VARIABLE_E_VALUE);
        expectedApplicationConfiguration.put(CONFIGURATION_E_KEY_3, CONFIGURATION_E_KEY3_VALUE);
        expectedApplicationConfiguration.put(CONFIGURATION_F, ENV_VARIABLE_F_VALUE);
        expectedApplicationConfiguration.put(CONFIGURATION_H, CONFIGURATION_H_OVERRIDDEN_VALUE);
        expectedApplicationConfiguration.put(CONFIGURATION_G, CONFIGURATION_G_OVERRIDDEN_VALUE);

        assertResolvedApplicationConfiguration(applicationConfiguration, expectedApplicationConfiguration);
    }

    @Test
    public void shouldResolveSimplePropertiesConfiguration() {
        // given
        var configurationFile = resolveConfigurationFileFromClasspath(SIMPLE_PROPERTIES_CONFIGURATION_FILE_PATH);
        var application = new TestApplication(configurationFile);

        // when
        var applicationConfiguration = resolver.resolve(application, List.of());

        // then
        var expectedApplicationConfiguration = Map.<String, Object>of(
                CONFIGURATION_A, CONFIGURATION_A_VALUE,
                CONFIGURATION_B, CONFIGURATION_B_VALUE,
                CONFIGURATION_D, CONFIGURATION_D_VALUE
        );

        assertResolvedApplicationConfiguration(applicationConfiguration, expectedApplicationConfiguration);
    }

    @Test
    public void shouldResolveExternalizedPropertiesConfiguration() {
        // given
        var configurationFile = resolveConfigurationFileFromClasspath(EXTERNALIZED_PROPERTIES_CONFIGURATION_FILE_PATH);
        var application = new TestApplication(configurationFile);

        var externalConfigurationRegistryA = new MapConfigurationRegistry(
                Set.of(
                        ConfigurationEntry.envVariable(ENV_VARIABLE_D, ENV_VARIABLE_D_VALUE)
                )
        );

        var externalConfigurationRegistryB = new MapConfigurationRegistry(
                Set.of(
                        ConfigurationEntry.property(CONFIGURATION_G, CONFIGURATION_G_OVERRIDDEN_VALUE)
                )
        );

        // when
        var applicationConfiguration = resolver.resolve(
                application,
                List.of(
                        externalConfigurationRegistryA,
                        externalConfigurationRegistryB
                )
        );

        // then
        var expectedApplicationConfiguration = new HashMap<String, Object>();
        expectedApplicationConfiguration.put(CONFIGURATION_A, CONFIGURATION_A_VALUE);
        expectedApplicationConfiguration.put(CONFIGURATION_D, ENV_VARIABLE_D_VALUE);
        expectedApplicationConfiguration.put(CONFIGURATION_G, CONFIGURATION_G_OVERRIDDEN_VALUE);

        assertResolvedApplicationConfiguration(applicationConfiguration, expectedApplicationConfiguration);
    }

    private Path resolveConfigurationFileFromClasspath(String filePath) {
        try {
            var resolvedFilePath = this
                    .getClass()
                    .getClassLoader()
                    .getResource(filePath)
                    .toURI();

            return Path.of(resolvedFilePath);
        } catch (Exception ex) {
            throw new IllegalStateException("Could not resolve configuration file from classpath", ex);
        }
    }
    
    private void assertResolvedApplicationConfiguration(Map<String, Object> applicationConfiguration, Map<String, Object> expectedApplicationConfiguration) {
        assertThat(applicationConfiguration).containsExactlyInAnyOrderEntriesOf(expectedApplicationConfiguration);
    }

}
