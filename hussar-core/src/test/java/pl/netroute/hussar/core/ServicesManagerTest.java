package pl.netroute.hussar.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.netroute.hussar.core.api.Service;
import pl.netroute.hussar.core.domain.ServiceTestA;
import pl.netroute.hussar.core.domain.ServiceTestB;
import pl.netroute.hussar.core.domain.ServiceTestC;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class ServicesManagerTest {
    private static final String FIRST_SERVICE_A_NAME = "first-serviceA-name";
    private static final String SECOND_SERVICE_A_NAME = "second-serviceA-name";

    private static final String FIRST_SERVICE_B_NAME = "first-serviceB-name";

    private ServicesManager servicesManager;

    @BeforeEach
    public void setup() {
        var firstServiceA = new ServiceTestA(FIRST_SERVICE_A_NAME);
        var secondServiceA = new ServiceTestA(SECOND_SERVICE_A_NAME);
        var firstServiceB = new ServiceTestB(FIRST_SERVICE_B_NAME);

        var servicesConfiguration = new ServicesConfiguration(List.of(firstServiceA, secondServiceA, firstServiceB));

        servicesManager = new ServicesManager(servicesConfiguration);
    }

    @Test
    public void shouldReturnEmptyWhenNoTypedServiceExists() {
        // given
        var typedService = ServiceTestC.class;

        // when
        var foundService = servicesManager.findByType(typedService);

        // then
        assertNoServiceFound(foundService);
    }

    @Test
    public void shouldReturnEmptyWhenNoNamedServiceExists() {
        // given
        var namedService = "some-name";

        // when
        var foundService = servicesManager.findByName(namedService);

        // then
        assertNoServiceFound(foundService);
    }

    @Test
    public void shouldReturnTypedService() {
        // given
        var typedService = ServiceTestB.class;

        // when
        var foundService = servicesManager.findByType(typedService);

        // then
        assertTypedServiceFound(foundService, typedService);
    }

    @Test
    public void shouldReturnNamedService() {
        // given
        var namedService = SECOND_SERVICE_A_NAME;

        // when
        var foundService = servicesManager.findByName(namedService);

        // then
        assertNamedServiceFound(foundService, namedService);
    }

    private void assertNamedServiceFound(Optional<? extends Service> service, String expectedServiceName) {
        assertThat(service).hasValueSatisfying(
                actualService -> assertThat(actualService.getName()).isEqualTo(expectedServiceName)
        );
    }

    private void assertTypedServiceFound(Optional<? extends Service> service, Class<? extends Service> expectedServiceType) {
        assertThat(service).hasValueSatisfying(
                actualService -> assertThat(actualService).isInstanceOf(expectedServiceType)
        );
    }

    private void assertNoServiceFound(Optional<? extends Service> service) {
        assertThat(service).isEmpty();
    }

}
