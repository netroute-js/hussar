package pl.netroute.hussar.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.netroute.hussar.core.service.api.HussarService;
import pl.netroute.hussar.core.service.api.ServiceRegistry;
import pl.netroute.hussar.core.domain.ServiceTestA;
import pl.netroute.hussar.core.domain.ServiceTestB;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ServiceInjectorTest {
    private ServiceRegistry serviceRegistry;
    private ServiceInjector serviceInjector;

    @BeforeEach
    public void setup() {
        serviceRegistry = mock(ServiceRegistry.class);

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
        var serviceTypeA = ServiceTestA.class;
        var serviceA = mock(ServiceTestA.class);

        var serviceNameB = "some-serviceB";
        var serviceB = mock(ServiceTestB.class);

        var testInstance = new TestClassWithServices();

        when(serviceRegistry.findEntryByType(serviceTypeA)).thenReturn(Optional.of(serviceA));
        when(serviceRegistry.findEntryByName(serviceNameB)).thenReturn(Optional.of(serviceB));

        // when
        serviceInjector.inject(testInstance);

        // then
        assertServiceLookupByTypePerformed(serviceRegistry);
        assertServiceLookupByNamePerformed(serviceRegistry);
        assertServicesInjected(testInstance, serviceA, serviceB);
    }

    @Test
    public void shouldInjectServiceWhenInheritancePresent() {
        // given
        var serviceTypeA = ServiceTestA.class;
        var serviceA = mock(ServiceTestA.class);

        var serviceTypeB = ServiceTestB.class;
        var serviceB = mock(ServiceTestB.class);

        var testInstance = new SubTestClassWithServices();

        when(serviceRegistry.findEntryByType(serviceTypeA)).thenReturn(Optional.of(serviceA));
        when(serviceRegistry.findEntryByType(serviceTypeB)).thenReturn(Optional.of(serviceB));

        // when
        serviceInjector.inject(testInstance);

        // then
        var expectedTypeLookups = 3;

        assertServiceLookupByTypePerformed(serviceRegistry, expectedTypeLookups);
        assertServicesInjected(testInstance, serviceA, serviceB);
    }

    @Test
    public void shouldFailInjectingServiceWhenServiceNotFoundByType() {
        // given
        var serviceType = ServiceTestA.class;

        var testInstance = new TestClassWithTypeService();

        when(serviceRegistry.findEntryByType(serviceType)).thenReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> serviceInjector.inject(testInstance))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(String.format("Expected exactly one HussarService of %s type", serviceType));

        assertServiceLookupByTypePerformed(serviceRegistry);
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

        assertServiceLookupByNamePerformed(serviceRegistry);
    }

    private void assertNoServiceLookupPerformed(ServiceRegistry serviceRegistry) {
        verify(serviceRegistry, never()).findEntryByName(anyString());
        verify(serviceRegistry, never()).findEntryByType(any());
    }

    private void assertServiceLookupByNamePerformed(ServiceRegistry serviceRegistry) {
        verify(serviceRegistry).findEntryByName(anyString());
    }

    private void assertServiceLookupByTypePerformed(ServiceRegistry serviceRegistry) {
        verify(serviceRegistry).findEntryByType(any());
    }

    private void assertServiceLookupByTypePerformed(ServiceRegistry serviceRegistry, int numberOfLookups) {
        verify(serviceRegistry, times(numberOfLookups)).findEntryByType(any());
    }

    private void assertServicesInjected(TestClassWithServices testInstance, ServiceTestA expectedServiceA, ServiceTestB expectedServiceB) {
        assertThat(testInstance.serviceA).isEqualTo(expectedServiceA);
        assertThat(testInstance.serviceB).isEqualTo(expectedServiceB);
        assertThat(testInstance.anotherServiceA).isNull();
        assertThat(testInstance.anotherServiceB).isNull();
    }

    private void assertServicesInjected(SubTestClassWithServices testInstance, ServiceTestA expectedServiceA, ServiceTestB expectedServiceB) {
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
        private ServiceTestA serviceA;

    }

    private static class TestClassWithNameService {

        @HussarService(name = "some-serviceB")
        ServiceTestB serviceB;

    }

    private static class TestClassWithServices {

        @HussarService
        private ServiceTestA serviceA;

        @HussarService(name = "some-serviceB")
        ServiceTestB serviceB;

        ServiceTestA anotherServiceA;
        ServiceTestB anotherServiceB;

    }

    private static class BaseTestClassWithServices {

        @HussarService
        ServiceTestA baseServiceA;

        ServiceTestA baseAnotherServiceA;
    }

    private static class SubTestClassWithServices extends BaseTestClassWithServices {

        @HussarService
        ServiceTestA serviceA;

        @HussarService
        ServiceTestB serviceB;

        ServiceTestA anotherServiceA;
    }

}
