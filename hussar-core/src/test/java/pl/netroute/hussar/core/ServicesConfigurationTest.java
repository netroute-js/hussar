package pl.netroute.hussar.core;

import org.junit.jupiter.api.Test;
import pl.netroute.hussar.core.api.Service;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ServicesConfigurationTest {

    @Test
    public void shouldBuildConfiguration() {
        // given
        var firstServiceA = new ServiceA("first-serviceA");
        var secondServiceA = new ServiceA("second-serviceA");
        var serviceB = new ServiceB("serviceB");
        var serviceC = new ServiceC();

        var services = List.<Service>of(firstServiceA, secondServiceA, serviceB, serviceC);

        // when
        var configuration = new ServicesConfiguration(services);

        // then
        assertServicesPresent(configuration, services);
    }

    @Test
    public void shouldFailBuildingConfigurationWhenMultipleServicesWithSameName() {
        // given
        var serviceA = new ServiceA("some-service");
        var serviceB = new ServiceB("some-service");
        var serviceC = new ServiceC("serviceC");

        // when
        // then
        assertThatThrownBy(() -> new ServicesConfiguration(List.of(serviceA, serviceB, serviceC)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Multiple services of the same name detected. Expected all of them to uniquely named - [some-service]");
    }

    @Test
    public void shouldFailBuildingConfigurationWhenMultipleTypedServicesButNameMissing() {
        // given
        var firstServiceA = new ServiceA("some-service");
        var secondServiceA = new ServiceA();
        var serviceC = new ServiceC();

        // when
        // then
        assertThatThrownBy(() -> new ServicesConfiguration(List.of(firstServiceA, secondServiceA, serviceC)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Multiple services of the same type detected. Expected all of them to be named - [pl.netroute.hussar.core.ServicesConfigurationTest.ServiceA]");
    }

    private void assertServicesPresent(ServicesConfiguration configuration, List<Service> expectedServices) {
        assertThat(configuration.getStandaloneServices()).containsExactlyInAnyOrderElementsOf(expectedServices);
    }

    private static abstract class AbstractService implements Service {
        private final String name;

        public AbstractService(String name) {
            this.name = name;
        }

        @Override
        public List<Endpoint> getEndpoints() {
            return List.of();
        }

        @Override
        public void start() {
        }

        @Override
        public void shutdown() {
        }

        @Override
        public String getName() {
            return name;
        }

    }

    private static class ServiceA extends AbstractService {

        public ServiceA() {
            this(null);
        }

        public ServiceA(String name) {
            super(name);
        }

    }

    private static class ServiceB extends AbstractService {

        public ServiceB() {
            this(null);
        }

        public ServiceB(String name) {
            super(name);
        }

    }

    private static class ServiceC extends AbstractService {

        public ServiceC() {
            this(null);
        }

        public ServiceC(String name) {
            super(name);
        }

    }

}
