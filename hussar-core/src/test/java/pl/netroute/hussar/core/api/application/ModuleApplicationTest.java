package pl.netroute.hussar.core.api.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.netroute.hussar.core.api.Endpoint;
import pl.netroute.hussar.core.application.ApplicationStartupContext;
import pl.netroute.hussar.core.application.api.ModuleApplication;
import pl.netroute.hussar.core.dependency.NoOpDependencyInjector;
import pl.netroute.hussar.core.dependency.api.DependencyInjector;

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
    public void shouldRestartApplication() {
        // given
        // when
        application.restart();

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

    @Test
    public void shouldReturnDependencyInjector() {
        // given
        // when
        var dependencyInjector = application.getDependencyInjector();

        // then
        assertDependencyInjector(dependencyInjector);
    }

    private void assertInitialized(boolean initialized) {
        assertThat(initialized).isTrue();
    }

    private void assertNoEndpointExists(List<Endpoint> endpoints) {
        assertThat(endpoints).isEmpty();
    }

    private void assertDependencyInjector(DependencyInjector dependencyInjector) {
        assertThat(dependencyInjector).isInstanceOf(NoOpDependencyInjector.class);
    }

}
