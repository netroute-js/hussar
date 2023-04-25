package pl.netroute.hussar.core;

import org.junit.jupiter.api.Test;
import pl.netroute.hussar.core.api.Service;
import pl.netroute.hussar.core.api.ServiceStartupContext;
import pl.netroute.hussar.core.domain.ServiceTestA;
import pl.netroute.hussar.core.domain.ServiceTestB;
import pl.netroute.hussar.core.domain.ServiceTestC;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ServicesConfigurationTest {

    @Test
    public void shouldBuildConfiguration() {
        // given
        var firstServiceA = new ServiceTestA("first-serviceA");
        var secondServiceA = new ServiceTestB("second-serviceA");
        var serviceB = new ServiceTestB("serviceB");
        var serviceC = new ServiceTestC();

        var services = List.<Service>of(firstServiceA, secondServiceA, serviceB, serviceC);

        // when
        var configuration = new ServicesConfiguration(services);

        // then
        assertServicesPresent(configuration, services);
    }

    @Test
    public void shouldFailBuildingConfigurationWhenMultipleServicesWithSameName() {
        // given
        var serviceA = new ServiceTestA("some-service");
        var serviceB = new ServiceTestB("some-service");
        var serviceC = new ServiceTestC("serviceC");

        // when
        // then
        assertThatThrownBy(() -> new ServicesConfiguration(List.of(serviceA, serviceB, serviceC)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Multiple services of the same name detected. Expected all of them to uniquely named - [some-service]");
    }

    @Test
    public void shouldFailBuildingConfigurationWhenMultipleTypedServicesButNameMissing() {
        // given
        var firstServiceA = new ServiceTestA("some-service");
        var secondServiceA = new ServiceTestA();
        var serviceC = new ServiceTestC();

        // when
        // then
        assertThatThrownBy(() -> new ServicesConfiguration(List.of(firstServiceA, secondServiceA, serviceC)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Multiple services of the same type detected. Expected all of them to be named - [pl.netroute.hussar.core.domain.ServiceTestA]");
    }

    private void assertServicesPresent(ServicesConfiguration configuration, List<Service> expectedServices) {
        assertThat(configuration.getStandaloneServices()).containsExactlyInAnyOrderElementsOf(expectedServices);
    }

}
