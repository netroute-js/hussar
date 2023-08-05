package pl.netroute.hussar.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.netroute.hussar.core.api.MapServiceRegistry;
import pl.netroute.hussar.core.api.Service;
import pl.netroute.hussar.core.api.ServiceStartupContext;
import pl.netroute.hussar.core.domain.ServiceTestA;
import pl.netroute.hussar.core.domain.ServiceTestB;

import java.util.Set;
import java.util.concurrent.ForkJoinPool;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ServiceStarterTest {
    private ServiceTestA serviceA;
    private ServiceTestB serviceB;

    private ServiceStarter starter;

    @BeforeEach
    public void setup() {
        starter = new ServiceStarter(ForkJoinPool.commonPool());
    }

    @Test
    public void shouldStartStandaloneServices() {
        // given
        serviceA = mock(ServiceTestA.class);
        serviceB = mock(ServiceTestB.class);
        var standaloneServices = Set.<Service>of(serviceA, serviceB);

        var serviceRegistry = new MapServiceRegistry(standaloneServices);

        // when
        starter.start(serviceRegistry);

        // then
        assertServiceStarted(serviceA);
        assertServiceStarted(serviceB);
    }

    private void assertServiceStarted(Service service) {
        verify(service).start(isA(ServiceStartupContext.class));
    }

}
