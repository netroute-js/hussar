package pl.netroute.hussar.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.netroute.hussar.core.domain.TestApplication;

import java.nio.file.Path;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ApplicationConfigurationLoaderTest {
    private static final String PROPERTIES_CONFIGURATION_FILE_PATH = "application.properties";
    private static final String YAML_CONFIGURATION_FILE_PATH = "application.yaml";
    private static final String YML_CONFIGURATION_FILE_PATH = "application.yml";
    private static final String UNSUPPORTED_CONFIGURATION_FILE_PATH = "application.txt";

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

    private static final String CONFIGURATION_A_VALUE = "some-valueA";
    private static final String CONFIGURATION_B_VALUE = "some-valueB";
    private static final String CONFIGURATION_D_VALUE = "some-valueD";
    private static final String CONFIGURATION_C_0_VALUE = "some-valueC1";
    private static final String CONFIGURATION_C_1_VALUE = "some-valueC2";
    private static final String CONFIGURATION_C_2_VALUE = "some-valueC3";
    private static final int CONFIGURATION_E_KEY1_VALUE = 1;
    private static final int CONFIGURATION_E_KEY2_VALUE = 2;
    private static final int CONFIGURATION_E_KEY3_VALUE = 3;

    private ApplicationConfigurationLoader loader;

    @BeforeEach
    public void setup() {
        var configurationFlattener = new ApplicationConfigurationFlattener();

        loader = new ApplicationConfigurationLoader(configurationFlattener);
    }

    @Test
    public void shouldLoadYmlApplicationConfiguration() {
        // given
        var configurationFile = resolveConfigurationFileFromClasspath(YML_CONFIGURATION_FILE_PATH);
        var application = new TestApplication(configurationFile);

        // when
        var applicationConfiguration = loader.load(application);

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

        assertLoadedApplicationConfiguration(applicationConfiguration, expectedApplicationConfiguration);
    }

    @Test
    public void shouldLoadYamlApplicationConfiguration() {
        // given
        var configurationFile = resolveConfigurationFileFromClasspath(YAML_CONFIGURATION_FILE_PATH);
        var application = new TestApplication(configurationFile);

        // when
        var applicationConfiguration = loader.load(application);

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

        assertLoadedApplicationConfiguration(applicationConfiguration, expectedApplicationConfiguration);
    }

    @Test
    public void shouldLoadPropertiesApplicationConfiguration() {
        // given
        var configurationFile = resolveConfigurationFileFromClasspath(PROPERTIES_CONFIGURATION_FILE_PATH);
        var application = new TestApplication(configurationFile);

        // when
        var applicationConfiguration = loader.load(application);

        // then
        var expectedApplicationConfiguration = Map.<String, Object>of(
                CONFIGURATION_A, CONFIGURATION_A_VALUE,
                CONFIGURATION_B, CONFIGURATION_B_VALUE,
                CONFIGURATION_D, CONFIGURATION_D_VALUE
        );

        assertLoadedApplicationConfiguration(applicationConfiguration, expectedApplicationConfiguration);
    }

    @Test
    public void shouldLoadEmptyApplicationConfigurationWhenConfigurationFileNotConfigured() {
        // given
        var application = new TestApplication();

        // when
        var applicationConfiguration = loader.load(application);

        // then
        assertEmptyApplicationConfiguration(applicationConfiguration);
    }

    @Test
    public void shouldFailLoadingApplicationConfigurationWhenUnsupportedConfigurationFile() {
        // given
        var configurationFile = resolveConfigurationFileFromClasspath(UNSUPPORTED_CONFIGURATION_FILE_PATH);
        var application = new TestApplication(configurationFile);

        // when
        var failure = assertThatThrownBy(() ->loader.load(application));

        // then
        failure
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Expected application configuration file to be yaml or properties file");
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

    private void assertLoadedApplicationConfiguration(Map<String, Object> applicationConfiguration, Map<String, Object> expectedApplicationConfiguration) {
        assertThat(applicationConfiguration).containsExactlyInAnyOrderEntriesOf(expectedApplicationConfiguration);
    }

    private void assertEmptyApplicationConfiguration(Map<String, Object> applicationConfiguration) {
        assertThat(applicationConfiguration).isEmpty();
    }

}
