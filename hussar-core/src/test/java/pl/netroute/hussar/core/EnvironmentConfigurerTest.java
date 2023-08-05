package pl.netroute.hussar.core;

import org.junit.jupiter.api.Test;
import pl.netroute.hussar.core.api.Application;
import pl.netroute.hussar.core.api.ConfigurationEntry;
import pl.netroute.hussar.core.api.Service;
import pl.netroute.hussar.core.domain.ServiceTestA;
import pl.netroute.hussar.core.domain.ServiceTestB;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

public class EnvironmentConfigurerTest {

    @Test
    public void shouldConfigureWithFullConfiguration() {
        // given
        var propertyA = "propertyA";
        var propertyValueA = "property value A";

        var envVariableB = "SOME_ENV_VARIABLE_B";
        var envVariableValueB = "some env variable value B";

        var standaloneServiceA = mock(ServiceTestA.class);
        var standaloneServiceB = mock(ServiceTestB.class);

        var application = mock(Application.class);

        // when
        var environment = EnvironmentConfigurer
                .newConfigurer()
                .withStaticConfigurationEntry(ConfigurationEntry.property(propertyA, propertyValueA))
                .withStaticConfigurationEntry(ConfigurationEntry.envVariable(envVariableB, envVariableValueB))
                .withStandaloneService(standaloneServiceA)
                .withStandaloneService(standaloneServiceB)
                .withApplication(application)
                .configure();

        // then
        var expectedServices = Set.<Service>of(standaloneServiceA, standaloneServiceB);
        var expectedConfigurationEntries = Set.<ConfigurationEntry>of(
                ConfigurationEntry.property(propertyA, propertyValueA),
                ConfigurationEntry.envVariable(envVariableB, envVariableValueB)
        );

        assertApplicationConfigured(environment, application);
        assertStandaloneServicesConfigured(environment, expectedServices);
        assertStaticConfigurationEntriesSetup(environment, expectedConfigurationEntries);
    }

    @Test
    public void shouldConfigureWithMinimalConfiguration() {
        // given
        var application = mock(Application.class);

        // when
        var environment = EnvironmentConfigurer
                .newConfigurer()
                .withApplication(application)
                .configure();

        // then
        assertApplicationConfigured(environment, application);
        assertNoStandaloneServicesConfigured(environment);
        assertNoStaticConfigurationEntriesSetup(environment);
    }

    @Test
    public void shouldFailConfiguringWhenApplicationMissing() {
        // given
        // when
        // then
        assertThatThrownBy(() -> EnvironmentConfigurer.newConfigurer().configure())
                .isInstanceOf(NullPointerException.class)
                .hasMessage("application needs to be configured");
    }

    private void assertApplicationConfigured(Environment environment, Application expectedApplication) {
        assertThat(environment.getApplication()).isEqualTo(expectedApplication);
    }

    private void assertStandaloneServicesConfigured(Environment environment, Set<Service> expectedServices) {
        assertThat(environment.getServiceRegistry().getEntries()).containsExactlyInAnyOrderElementsOf(expectedServices);
    }

    private void assertStaticConfigurationEntriesSetup(Environment environment, Set<ConfigurationEntry> expectedConfigEntries) {
        assertThat(environment.getStaticConfigurationRegistry().getEntries()).containsExactlyInAnyOrderElementsOf(expectedConfigEntries);
    }

    private void assertNoStandaloneServicesConfigured(Environment environment) {
        assertThat(environment.getServiceRegistry().getEntries()).isEmpty();
    }

    private void assertNoStaticConfigurationEntriesSetup(Environment environment) {
        assertThat(environment.getStaticConfigurationRegistry().getEntries()).isEmpty();
    }

}
