package pl.netroute.hussar.core.environment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.netroute.hussar.core.service.ServiceStartupContext;
import pl.netroute.hussar.core.service.api.DefaultServiceRegistry;
import pl.netroute.hussar.core.service.api.Service;
import pl.netroute.hussar.core.test.stub.ServiceStubA;
import pl.netroute.hussar.core.test.stub.ServiceStubB;

import java.util.concurrent.ForkJoinPool;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.verify;

public class ServiceStarterTest {
    private ServiceStarter starter;

    @BeforeEach
    public void setup() {
        starter = new ServiceStarter(ForkJoinPool.commonPool());
    }

    @Test
    public void shouldStartStandaloneServices() {
        // given
        var serviceA = ServiceStubA.defaultStub();
        var serviceB = ServiceStubB.defaultStub();
        var serviceRegistry = DefaultServiceRegistry.of(serviceA, serviceB);

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
