package pl.netroute.hussar.core.environment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.netroute.hussar.core.service.api.DefaultServiceRegistry;
import pl.netroute.hussar.core.service.api.Service;
import pl.netroute.hussar.core.test.stub.ServiceStubA;
import pl.netroute.hussar.core.test.stub.ServiceStubB;

import java.util.concurrent.ForkJoinPool;

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
        var serviceA = ServiceStubA.defaultStub();
        var serviceB = ServiceStubB.defaultStub();
        var servicesConfig = DefaultServiceRegistry.of(serviceA, serviceB);

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
