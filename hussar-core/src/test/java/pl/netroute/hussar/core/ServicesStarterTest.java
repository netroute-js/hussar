package pl.netroute.hussar.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.netroute.hussar.core.api.Service;
import pl.netroute.hussar.core.api.ServiceStartupContext;
import pl.netroute.hussar.core.domain.ServiceTestA;
import pl.netroute.hussar.core.domain.ServiceTestB;

import java.util.List;
import java.util.concurrent.ForkJoinPool;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ServicesStarterTest {
    private ServiceTestA serviceA;
    private ServiceTestB serviceB;

    private ServicesStarter starter;

    @BeforeEach
    public void setup() {
        starter = new ServicesStarter(ForkJoinPool.commonPool());
    }

    @Test
    public void shouldStartStandaloneServices() {
        // given
        serviceA = mock(ServiceTestA.class);
        serviceB = mock(ServiceTestB.class);
        var standaloneServices = List.<Service>of(serviceA, serviceB);

        var servicesConfig = new ServicesConfiguration(standaloneServices);

        // when
        starter.start(servicesConfig);

        // then
        assertServiceStarted(serviceA);
        assertServiceStarted(serviceB);
    }

    private void assertServiceStarted(Service service) {
        verify(service).start(isA(ServiceStartupContext.class));
    }

}
