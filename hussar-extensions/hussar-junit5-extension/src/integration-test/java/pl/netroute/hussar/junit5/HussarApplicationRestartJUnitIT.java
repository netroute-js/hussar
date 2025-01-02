package pl.netroute.hussar.junit5;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import pl.netroute.hussar.core.application.api.Application;
import pl.netroute.hussar.core.application.api.HussarApplication;
import pl.netroute.hussar.core.application.api.HussarApplicationRestart;
import pl.netroute.hussar.core.environment.api.HussarEnvironment;
import pl.netroute.hussar.junit5.client.SimpleApplicationClient;
import pl.netroute.hussar.junit5.config.SpringTestEnvironmentConfigurerProvider;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(HussarJUnit5Extension.class)
@HussarEnvironment(configurerProvider = SpringTestEnvironmentConfigurerProvider.class)
public class HussarApplicationRestartJUnitIT {

    @HussarApplication
    private Application application;

    @Test
    @Order(1)
    public void shouldChangeApplicationState() {
        // given
        var applicationClient = SimpleApplicationClient.newClient(application);

        // when
        var version = applicationClient.incrementVersion();

        // then
        var expectedVersion = 2;

        assertApplicationVersionedChanged(version, expectedVersion);
    }

    @Test
    @Order(2)
    @HussarApplicationRestart
    public void shouldRestartApplication() {
        // given
        var applicationClient = SimpleApplicationClient.newClient(application);

        // when
        var version = applicationClient.getVersion();

        // then
        assertApplicationVersionedReset(version);
    }

    private void assertApplicationVersionedChanged(int version, int expectedVersion) {
        assertThat(version).isEqualTo(expectedVersion);
    }

    private void assertApplicationVersionedReset(int version) {
        var defaultVersion = 1;

        assertThat(version).isEqualTo(defaultVersion);
    }

}
