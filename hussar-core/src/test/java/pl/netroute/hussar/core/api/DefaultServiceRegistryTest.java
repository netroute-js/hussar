package pl.netroute.hussar.core.api;

import org.junit.jupiter.api.Test;
import pl.netroute.hussar.core.service.api.DefaultServiceRegistry;
import pl.netroute.hussar.core.service.api.Service;
import pl.netroute.hussar.core.test.stub.ServiceStubA;
import pl.netroute.hussar.core.test.stub.ServiceStubB;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class DefaultServiceRegistryTest {
    private static final String NO_SERVICE_NAME = null;

    @Test
    public void shouldRegisterTypedService() {
        // given
        var service = ServiceStubA.defaultStub();
        var serviceRegistry = new DefaultServiceRegistry();

        // when
        serviceRegistry.register(service);

        // then
        assertTypedServiceRegistered(serviceRegistry, service);
    }

    @Test
    public void shouldFailRegisteringDuplicatedTypedService() {
        // given
        var service = ServiceStubA.newStub(NO_SERVICE_NAME);
        var duplicateService = ServiceStubA.newStub(NO_SERVICE_NAME);
        var serviceRegistry = DefaultServiceRegistry.of(service);

        // when
        // then
        assertThatThrownBy(() -> serviceRegistry.register(duplicateService))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Could not register typed service - pl.netroute.hussar.core.test.stub.ServiceStubA. If you want more services of the same type registered then all of them need to be named");
    }

    @Test
    public void shouldRegisterNamedService() {
        // given
        var service = ServiceStubA.defaultStub();
        var serviceRegistry = new DefaultServiceRegistry();

        // when
        serviceRegistry.register(service);

        // then
        assertNamedServiceRegistered(serviceRegistry, service);
    }

    @Test
    public void shouldRegisterMoreNamedServices() {
        // given
        var firstServiceA = ServiceStubA.defaultStub();
        var secondServiceA = ServiceStubA.defaultStub();
        var serviceB = ServiceStubB.defaultStub();

        var serviceRegistry = new DefaultServiceRegistry();

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
        var serviceName = "serviceA";
        var service = ServiceStubA.newStub(serviceName);
        var duplicateService = ServiceStubA.newStub(serviceName);
        var serviceRegistry = DefaultServiceRegistry.of(service);

        // when
        // then
        assertThatThrownBy(() -> serviceRegistry.register(duplicateService))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Could not register named service - %s. There is already a service registered with that name. Service name must be unique", serviceName);
    }

    @Test
    public void shouldReturnEmptyWhenNoTypedServiceExists() {
        // given
        var typedService = ServiceStubA.class;
        var serviceRegistry = new DefaultServiceRegistry();

        // when
        var foundService = serviceRegistry.findEntryByType(typedService);

        // then
        assertNoServiceFound(foundService);
    }

    @Test
    public void shouldReturnEmptyWhenNoNamedServiceExists() {
        // given
        var serviceRegistry = new DefaultServiceRegistry();
        var serviceName = "serviceA";

        // when
        var foundService = serviceRegistry.findEntryByName(serviceName);

        // then
        assertNoServiceFound(foundService);
    }

    @Test
    public void shouldReturnTypedService() {
        // given
        var typedService = ServiceStubB.defaultStub();
        var serviceRegistry = DefaultServiceRegistry.of(typedService);

        // when
        var foundService = serviceRegistry.findEntryByType(typedService.getClass());

        // then
        assertTypedServiceFound(foundService, typedService);
    }

    @Test
    public void shouldReturnNamedService() {
        // given
        var namedServiceA = ServiceStubA.defaultStub();
        var secondNamedServiceA = ServiceStubA.defaultStub();
        var namedServiceB = ServiceStubB.defaultStub();
        var serviceRegistry = DefaultServiceRegistry.of(namedServiceA, secondNamedServiceA, namedServiceB);

        // when
        var foundService = serviceRegistry.findEntryByName(secondNamedServiceA.getName());

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

    private void assertTypedServiceRegistered(DefaultServiceRegistry registry, Service expectedService) {
        long registeredServices = registry
                .getEntries()
                .stream()
                .filter(service -> service.equals(expectedService))
                .count();

        assertThat(registeredServices).isOne();
    }

    private void assertNamedServiceRegistered(DefaultServiceRegistry registry, Service expectedService) {
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
