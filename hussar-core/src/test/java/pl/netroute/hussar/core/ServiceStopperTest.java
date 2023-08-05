package pl.netroute.hussar.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.netroute.hussar.core.api.MapServiceRegistry;
import pl.netroute.hussar.core.api.Service;
import pl.netroute.hussar.core.domain.ServiceTestA;
import pl.netroute.hussar.core.domain.ServiceTestB;

import java.util.Set;
import java.util.concurrent.ForkJoinPool;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ServiceStopperTest {
    private ServiceTestA serviceA;
    private ServiceTestB serviceB;
    private ServiceStopper stopper;

    @BeforeEach
    public void setup() {
        stopper = new ServiceStopper(ForkJoinPool.commonPool());
    }

    @Test
    public void shouldStopStandaloneServices() {
        // given
        serviceA = mock(ServiceTestA.class);
        serviceB = mock(ServiceTestB.class);
        var standaloneServices = Set.<Service>of(serviceA, serviceB);

        var servicesConfig = new MapServiceRegistry(standaloneServices);

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
