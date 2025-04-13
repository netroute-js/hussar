package pl.netroute.hussar.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.netroute.hussar.core.service.api.HussarService;
import pl.netroute.hussar.core.service.api.ServiceRegistry;
import pl.netroute.hussar.core.test.stub.Mock;
import pl.netroute.hussar.core.test.stub.ServiceStubA;
import pl.netroute.hussar.core.test.stub.ServiceStubB;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ServiceInjectorTest {
    private ServiceRegistry serviceRegistry;
    private ServiceInjector serviceInjector;

    @BeforeEach
    public void setup() {
        serviceRegistry = Mock.defaultMock(ServiceRegistry.class);

        serviceInjector = new ServiceInjector(serviceRegistry);
    }

    @Test
    public void shouldSkipInjectingServicesWhenNoServicesInjectionRequired() {
        // given
        var testInstance = new TestClassWithoutServices();

        // when
        serviceInjector.inject(testInstance);

        // then
        assertNoServiceLookupPerformed(serviceRegistry);
    }

    @Test
    public void shouldInjectService() {
        // given
        var serviceTypeA = ServiceStubA.class;
        var serviceA = ServiceStubA.defaultStub();

        var serviceNameB = "some-serviceB";
        var serviceB = ServiceStubB.newStub(serviceNameB);

        var testInstance = new TestClassWithServices();

        when(serviceRegistry.findEntryByType(serviceTypeA)).thenReturn(Optional.of(serviceA));
        when(serviceRegistry.findEntryByName(serviceNameB)).thenReturn(Optional.of(serviceB));

        // when
        serviceInjector.inject(testInstance);

        // then
        assertServicesInjected(testInstance, serviceA, serviceB);
    }

    @Test
    public void shouldInjectServiceWhenInheritancePresent() {
        // given
        var serviceTypeA = ServiceStubA.class;
        var serviceA = ServiceStubA.defaultStub();

        var serviceTypeB = ServiceStubB.class;
        var serviceB = ServiceStubB.defaultStub();

        var testInstance = new SubTestClassWithServices();

        when(serviceRegistry.findEntryByType(serviceTypeA)).thenReturn(Optional.of(serviceA));
        when(serviceRegistry.findEntryByType(serviceTypeB)).thenReturn(Optional.of(serviceB));

        // when
        serviceInjector.inject(testInstance);

        // then
        assertServicesInjected(testInstance, serviceA, serviceB);
    }

    @Test
    public void shouldFailInjectingServiceWhenServiceNotFoundByType() {
        // given
        var serviceType = ServiceStubA.class;
        var testInstance = new TestClassWithTypeService();

        when(serviceRegistry.findEntryByType(serviceType)).thenReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> serviceInjector.inject(testInstance))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(String.format("Expected exactly one HussarService of %s type", serviceType));
    }

    @Test
    public void shouldFailInjectingServiceWhenServiceNotFoundByName() {
        // given
        var serviceName = "some-serviceB";
        var testInstance = new TestClassWithNameService();

        when(serviceRegistry.findEntryByName(serviceName)).thenReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> serviceInjector.inject(testInstance))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(String.format("Expected exactly one HussarService named %s", serviceName));
    }

    private void assertNoServiceLookupPerformed(ServiceRegistry serviceRegistry) {
        verify(serviceRegistry, never()).findEntryByName(anyString());
        verify(serviceRegistry, never()).findEntryByType(any());
    }

    private void assertServicesInjected(TestClassWithServices testInstance, ServiceStubA expectedServiceA, ServiceStubB expectedServiceB) {
        assertThat(testInstance.serviceA).isEqualTo(expectedServiceA);
        assertThat(testInstance.serviceB).isEqualTo(expectedServiceB);
        assertThat(testInstance.anotherServiceA).isNull();
        assertThat(testInstance.anotherServiceB).isNull();
    }

    private void assertServicesInjected(SubTestClassWithServices testInstance, ServiceStubA expectedServiceA, ServiceStubB expectedServiceB) {
        assertThat(testInstance.baseServiceA).isEqualTo(expectedServiceA);
        assertThat(testInstance.serviceA).isEqualTo(expectedServiceA);
        assertThat(testInstance.serviceB).isEqualTo(expectedServiceB);
        assertThat(testInstance.baseAnotherServiceA).isNull();
        assertThat(testInstance.anotherServiceA).isNull();
    }

    private static class TestClassWithoutServices {
    }

    private static class TestClassWithTypeService {

        @HussarService
        private ServiceStubA serviceA;

    }

    private static class TestClassWithNameService {

        @HussarService(name = "some-serviceB")
        ServiceStubB serviceB;

    }

    private static class TestClassWithServices {

        @HussarService
        private ServiceStubA serviceA;

        @HussarService(name = "some-serviceB")
        ServiceStubB serviceB;

        ServiceStubA anotherServiceA;
        ServiceStubB anotherServiceB;

    }

    private static class BaseTestClassWithServices {

        @HussarService
        ServiceStubA baseServiceA;

        ServiceStubA baseAnotherServiceA;
    }

    private static class SubTestClassWithServices extends BaseTestClassWithServices {

        @HussarService
        ServiceStubA serviceA;

        @HussarService
        ServiceStubB serviceB;

        ServiceStubA anotherServiceA;
    }

}
