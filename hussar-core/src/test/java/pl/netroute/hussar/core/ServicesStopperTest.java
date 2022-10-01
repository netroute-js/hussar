package pl.netroute.hussar.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.netroute.hussar.core.api.Service;

import java.util.List;
import java.util.concurrent.ForkJoinPool;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ServicesStopperTest {
    private ServicesStopper stopper;

    @BeforeEach
    public void setup() {
        stopper = new ServicesStopper(ForkJoinPool.commonPool());
    }

    @Test
    public void shouldStopStandaloneServices() {
        // given
        var serviceA = mock(Service.class);
        var serviceB = mock(Service.class);
        var standaloneServices = List.of(serviceA, serviceB);

        var servicesConfig = new ServicesConfiguration(standaloneServices);

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
