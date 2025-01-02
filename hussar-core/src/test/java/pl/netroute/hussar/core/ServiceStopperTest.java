package pl.netroute.hussar.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.netroute.hussar.core.service.api.DefaultServiceRegistry;
import pl.netroute.hussar.core.service.api.Service;
import pl.netroute.hussar.core.domain.ServiceTestA;
import pl.netroute.hussar.core.domain.ServiceTestB;

import java.util.Set;
import java.util.concurrent.ForkJoinPool;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ServiceStopperTest {
    private ServiceStopper stopper;

    @BeforeEach
    public void setup() {
        stopper = new ServiceStopper(ForkJoinPool.commonPool());
    }

    @Test
    public void shouldStopStandaloneServices() {
        // given
        var serviceA = mock(ServiceTestA.class);
        var serviceB = mock(ServiceTestB.class);
        var standaloneServices = Set.<Service>of(serviceA, serviceB);

        var servicesConfig = new DefaultServiceRegistry(standaloneServices);

        // when
        stopper.stop(servicesConfig);

        // then
        assertServiceStopped(serviceA);
        assertServiceStopped(serviceB);
    }

    private void assertServiceStopped(Service service) {
        verify(service).shutdown();
    }

}
