package pl.netroute.hussar.core;

import org.junit.jupiter.api.Test;
import pl.netroute.hussar.core.api.Application;
import pl.netroute.hussar.core.api.Service;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

public class EnvironmentConfigurerTest {

    @Test
    public void shouldConfigureWithFullConfiguration() {
        // given
        var propertyA = "propertyA";
        var propertyValueA = "property value A";

        var propertyB = "propertyB";
        var propertyValueB = "property value B";

        var standaloneServiceA = mock(Service.class);
        var standaloneServiceB = mock(Service.class);

        var application = mock(Application.class);

        // when
        var environment = EnvironmentConfigurer
                .newConfigurer()
                .withProperty(propertyA, propertyValueA)
                .withProperty(propertyB, propertyValueB)
                .withStandaloneService(standaloneServiceA)
                .withStandaloneService(standaloneServiceB)
                .withApplication(application)
                .configure();

        // then
        var expectedServices = List.of(standaloneServiceA, standaloneServiceB);
        var expectedProperties = Map.of(
                propertyA, propertyValueA,
                propertyB, propertyValueB
        );

        assertApplicationConfigured(environment, application);
        assertStandaloneServicesConfigured(environment, expectedServices);
        assertPropertiesConfigured(environment, expectedProperties);
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
        assertNoPropertiesConfigured(environment);
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

    private void assertStandaloneServicesConfigured(Environment environment, List<Service> expectedServices) {
        assertThat(environment.getServicesConfiguration().getStandaloneServices()).containsExactlyElementsOf(expectedServices);
    }

    private void assertPropertiesConfigured(Environment environment, Map<String, String> expectedProperties) {
        assertThat(environment.getPropertiesConfiguration().getProperties()).containsExactlyInAnyOrderEntriesOf(expectedProperties);
    }

    private void assertNoStandaloneServicesConfigured(Environment environment) {
        assertThat(environment.getServicesConfiguration().getStandaloneServices()).isEmpty();
    }

    private void assertNoPropertiesConfigured(Environment environment) {
        assertThat(environment.getPropertiesConfiguration().getProperties()).isEmpty();
    }

}
