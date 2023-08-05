package pl.netroute.hussar.core.api;

import org.assertj.core.api.AbstractThrowableAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.netroute.hussar.core.domain.ServiceTestA;
import pl.netroute.hussar.core.domain.ServiceTestB;
import pl.netroute.hussar.core.domain.ServiceTestC;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class MapServiceRegistryTest {
    private static final String FIRST_SERVICE_A_NAME = "first-serviceA-name";
    private static final String SECOND_SERVICE_A_NAME = "second-serviceA-name";

    private static final String FIRST_SERVICE_B_NAME = "first-serviceB-name";

    @Test
    public void shouldRegisterTypedService() {
        // given
        var service = new ServiceTestA();

        var serviceRegistry = new MapServiceRegistry();

        // when
        serviceRegistry.register(service);

        // then
        assertTypedServiceRegistered(serviceRegistry, service);
    }

    @Test
    public void shouldFailRegisteringDuplicatedTypedService() {
        // given
        var service = new ServiceTestA();
        var duplicateService = new ServiceTestA();

        var serviceRegistry = new MapServiceRegistry(Set.of(service));

        // when
        var failure = assertThatThrownBy(() -> serviceRegistry.register(duplicateService));


        // then
        failure
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Could not register typed service - pl.netroute.hussar.core.domain.ServiceTestA. If you want more services of the same type registered then all of them need to be named");
    }

    @Test
    public void shouldRegisterNamedService() {
        // given
        var service = new ServiceTestA(FIRST_SERVICE_A_NAME);

        var serviceRegistry = new MapServiceRegistry();

        // when
        serviceRegistry.register(service);

        // then
        assertNamedServiceRegistered(serviceRegistry, service);
    }

    @Test
    public void shouldRegisterMoreNamedServices() {
        // given
        var firstServiceA = new ServiceTestA(FIRST_SERVICE_A_NAME);
        var secondServiceA = new ServiceTestA(SECOND_SERVICE_A_NAME);
        var serviceB = new ServiceTestB(FIRST_SERVICE_B_NAME);

        var serviceRegistry = new MapServiceRegistry();

        // when
        serviceRegistry.register(firstServiceA);
        serviceRegistry.register(secondServiceA);
        serviceRegistry.register(serviceB);

        // then
        assertNamedServiceRegistered(serviceRegistry, firstServiceA);
        assertNamedServiceRegistered(serviceRegistry, secondServiceA);
        assertNamedServiceRegistered(serviceRegistry, serviceB);
    }

    @Test
    public void shouldFailRegisteringDuplicatedNamedService() {
        // given
        var service = new ServiceTestA(FIRST_SERVICE_A_NAME);
        var duplicateService = new ServiceTestA(FIRST_SERVICE_A_NAME);

        var serviceRegistry = new MapServiceRegistry(Set.of(service));

        // when
        var failure = assertThatThrownBy(() -> serviceRegistry.register(duplicateService));


        // then
        failure
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Could not register named service - %s. There is already a service registered with that name. Service name must be unique", FIRST_SERVICE_A_NAME);
    }

    @Test
    public void shouldReturnEmptyWhenNoTypedServiceExists() {
        // given
        var serviceRegistry = new MapServiceRegistry();
        var typedService = ServiceTestC.class;

        // when
        var foundService = serviceRegistry.findEntryByType(typedService);

        // then
        assertNoServiceFound(foundService);
    }

    @Test
    public void shouldReturnEmptyWhenNoNamedServiceExists() {
        // given
        var serviceRegistry = new MapServiceRegistry();
        var namedService = "some-name";

        // when
        var foundService = serviceRegistry.findEntryByName(namedService);

        // then
        assertNoServiceFound(foundService);
    }

    @Test
    public void shouldReturnTypedService() {
        // given
        var typedService = new ServiceTestB();

        var serviceRegistry = new MapServiceRegistry(Set.of(typedService));

        // when
        var foundService = serviceRegistry.findEntryByType(typedService.getClass());

        // then
        assertTypedServiceFound(foundService, typedService);
    }

    @Test
    public void shouldReturnNamedService() {
        // given
        var namedServiceA = new ServiceTestA(FIRST_SERVICE_A_NAME);
        var secondNamedServiceA = new ServiceTestA(SECOND_SERVICE_A_NAME);
        var namedServiceB = new ServiceTestB(FIRST_SERVICE_B_NAME);

        var serviceRegistry = new MapServiceRegistry(Set.of(namedServiceA, secondNamedServiceA, namedServiceB));

        // when
        var foundService = serviceRegistry.findEntryByName(SECOND_SERVICE_A_NAME);

        // then
        assertNamedServiceFound(foundService, secondNamedServiceA);
    }

    private void assertNamedServiceFound(Optional<? extends Service> service, Service expectedService) {
        assertThat(service).hasValueSatisfying(
                actualService -> assertThat(actualService).isEqualTo(expectedService)
        );
    }

    private void assertTypedServiceFound(Optional<? extends Service> service, Service expectedService) {
        assertThat(service).hasValueSatisfying(
                actualService -> assertThat(actualService).isEqualTo(expectedService)
        );
    }

    private void assertTypedServiceRegistered(MapServiceRegistry registry, Service expectedService) {
        long registeredServices = registry
                .getEntries()
                .stream()
                .filter(service -> service.equals(expectedService))
                .count();

        assertThat(registeredServices).isOne();
    }

    private void assertNamedServiceRegistered(MapServiceRegistry registry, Service expectedService) {
        long registeredServices = registry
                .getEntries()
                .stream()
                .filter(service -> service.equals(expectedService))
                .count();

        assertThat(registeredServices).isOne();
    }

    private void assertNoServiceFound(Optional<? extends Service> service) {
        assertThat(service).isEmpty();
    }
}
