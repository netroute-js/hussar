package pl.netroute.hussar.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.netroute.hussar.core.annotation.HussarService;
import pl.netroute.hussar.core.domain.ServiceTestA;
import pl.netroute.hussar.core.domain.ServiceTestB;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ServicesInjectorTest {
    private ServicesManager servicesManager;
    private ServicesInjector servicesInjector;

    @BeforeEach
    public void setup() {
        servicesManager = mock(ServicesManager.class);

        servicesInjector = new ServicesInjector(servicesManager);
    }

    @Test
    public void shouldSkipInjectingServicesWhenNoServicesInjectionRequired() {
        // given
        var testInstance = new TestClassWithoutServices();

        // when
        servicesInjector.inject(testInstance);

        // then
        assertNoServiceLookupPerformed(servicesManager);
    }

    @Test
    public void shouldInjectService() {
        // given
        var serviceTypeA = ServiceTestA.class;
        var serviceA = mock(ServiceTestA.class);

        var serviceNameB = "some-serviceB";
        var serviceB = mock(ServiceTestB.class);

        var testInstance = new TestClassWithServices();

        when(servicesManager.findByType(serviceTypeA)).thenReturn(Optional.of(serviceA));
        when(servicesManager.findByName(serviceNameB)).thenReturn(Optional.of(serviceB));

        // when
        servicesInjector.inject(testInstance);

        // then
        assertServiceLookupByTypePerformed(servicesManager);
        assertServiceLookupByNamePerformed(servicesManager);
        assertServicesInjected(testInstance, serviceA, serviceB);
    }

    @Test
    public void shouldFailInjectingServiceWhenServiceNotFoundByType() {
        // given
        var serviceType = ServiceTestA.class;

        var testInstance = new TestClassWithTypeService();

        when(servicesManager.findByType(serviceType)).thenReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> servicesInjector.inject(testInstance))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(String.format("Expected exactly one HussarService of %s type", serviceType));

        assertServiceLookupByTypePerformed(servicesManager);
    }

    @Test
    public void shouldFailInjectingServiceWhenServiceNotFoundByName() {
        // given
        var serviceName = "some-serviceB";

        var testInstance = new TestClassWithNameService();

        when(servicesManager.findByName(serviceName)).thenReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> servicesInjector.inject(testInstance))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(String.format("Expected exactly one HussarService named %s", serviceName));

        assertServiceLookupByNamePerformed(servicesManager);
    }

    private void assertNoServiceLookupPerformed(ServicesManager servicesManager) {
        verify(servicesManager, never()).findByName(anyString());
        verify(servicesManager, never()).findByType(any());
    }

    private void assertServiceLookupByNamePerformed(ServicesManager servicesManager) {
        verify(servicesManager).findByName(anyString());
    }

    private void assertServiceLookupByTypePerformed(ServicesManager servicesManager) {
        verify(servicesManager).findByType(any());
    }

    private void assertServicesInjected(TestClassWithServices testInstance, ServiceTestA expectedServiceA, ServiceTestB expectedServiceB) {
        assertThat(testInstance.serviceA).isEqualTo(expectedServiceA);
        assertThat(testInstance.serviceB).isEqualTo(expectedServiceB);
        assertThat(testInstance.anotherServiceA).isNull();
        assertThat(testInstance.anotherServiceB).isNull();
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

}
