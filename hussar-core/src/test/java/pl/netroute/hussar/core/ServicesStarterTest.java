package pl.netroute.hussar.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.netroute.hussar.core.api.Service;

import java.util.List;
import java.util.concurrent.ForkJoinPool;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ServicesStarterTest {
    private ServicesStarter starter;

    @BeforeEach
    public void setup() {
        starter = new ServicesStarter(ForkJoinPool.commonPool());
    }

    @Test
    public void shouldStartStandaloneServices() {
        // given
        var serviceA = mock(Service.class);
        var serviceB = mock(Service.class);
        var standaloneServices = List.of(serviceA, serviceB);

        var servicesConfig = new ServicesConfiguration(standaloneServices);

        // when
        starter.start(servicesConfig);

        // then
        assertServiceStarted(serviceA);
        assertServiceStarted(serviceB);
    }

    private void assertServiceStarted(Service service) {
        verify(service).start();
    }

}
