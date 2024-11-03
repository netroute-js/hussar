package pl.netroute.hussar.core.api.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.netroute.hussar.core.api.Endpoint;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class ModuleApplicationTest {
    private ModuleApplication application;

    @BeforeEach
    public void setup()  {
        application = ModuleApplication.newApplication();
    }

    @Test
    public void shouldStartApplication() {
        // given
        var startupContext = new ApplicationStartupContext(Set.of());

        // when
        application.start(startupContext);

        // then
        // nothing to assert on
    }

    @Test
    public void shouldShutdownApplication() {
        // given
        // when
        application.shutdown();

        // then
        // nothing to assert on
    }

    @Test
    public void shouldReturnInitialized() {
        // given
        // when
        var initialized = application.isInitialized();

        // then
        assertInitialized(initialized);
    }

    @Test
    public void shouldReturnNoEndpoints() {
        // given
        // when
        var endpoints = application.getEndpoints();

        // then
        assertNoEndpointExists(endpoints);
    }

    private void assertInitialized(boolean initialized) {
        assertThat(initialized).isTrue();
    }

    private void assertNoEndpointExists(List<Endpoint> endpoints) {
        assertThat(endpoints).isEmpty();
    }
}
