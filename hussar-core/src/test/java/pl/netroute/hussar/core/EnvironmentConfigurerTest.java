package pl.netroute.hussar.core;

import org.junit.jupiter.api.Test;
import pl.netroute.hussar.core.application.api.Application;
import pl.netroute.hussar.core.configuration.api.ConfigurationEntry;
import pl.netroute.hussar.core.domain.ServiceTestA;
import pl.netroute.hussar.core.domain.ServiceTestB;
import pl.netroute.hussar.core.domain.StubServiceConfigurer;
import pl.netroute.hussar.core.environment.api.Environment;
import pl.netroute.hussar.core.environment.api.LocalEnvironmentConfigurer;
import pl.netroute.hussar.core.service.api.Service;

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

        var standaloneServiceA = new StubServiceConfigurer<>(ServiceTestA.class);
        var standaloneServiceB = new StubServiceConfigurer<>(ServiceTestB.class);

        var application = mock(Application.class);

        // when
        var environment = LocalEnvironmentConfigurer
                .newInstance()
                .withProperty(propertyA, propertyValueA)
                .withEnvironmentVariable(envVariableB, envVariableValueB)
                .withService(standaloneServiceA)
                .withService(standaloneServiceB)
                .withApplication(application)
                .done()
                .configure();

        // then
        var expectedServices = Set.<Service>of(standaloneServiceA.getService(), standaloneServiceB.getService());
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
        var environment = LocalEnvironmentConfigurer
                .newInstance()
                .withApplication(application)
                .done()
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
        assertThatThrownBy(() -> LocalEnvironmentConfigurer
                    .newInstance()
                    .done()
                    .configure())
                .isInstanceOf(NullPointerException.class)
                .hasMessage("application is marked non-null but is null");
    }

    private void assertApplicationConfigured(Environment environment, Application expectedApplication) {
        assertThat(environment.application()).isEqualTo(expectedApplication);
    }

    private void assertStandaloneServicesConfigured(Environment environment, Set<Service> expectedServices) {
        assertThat(environment.serviceRegistry().getEntries()).containsExactlyInAnyOrderElementsOf(expectedServices);
    }

    private void assertStaticConfigurationEntriesSetup(Environment environment, Set<ConfigurationEntry> expectedConfigEntries) {
        assertThat(environment.configurationRegistry().getEntries()).containsExactlyInAnyOrderElementsOf(expectedConfigEntries);
    }

    private void assertNoStandaloneServicesConfigured(Environment environment) {
        assertThat(environment.serviceRegistry().getEntries()).isEmpty();
    }

    private void assertNoStaticConfigurationEntriesSetup(Environment environment) {
        assertThat(environment.configurationRegistry().getEntries()).isEmpty();
    }

}
