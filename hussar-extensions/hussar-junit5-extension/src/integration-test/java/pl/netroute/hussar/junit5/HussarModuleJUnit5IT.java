package pl.netroute.hussar.junit5;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import pl.netroute.hussar.core.api.application.Application;
import pl.netroute.hussar.core.api.application.HussarApplication;
import pl.netroute.hussar.core.api.environment.HussarEnvironment;
import pl.netroute.hussar.core.api.service.HussarService;
import pl.netroute.hussar.junit5.config.ModuleTestEnvironmentConfigurerProvider;
import pl.netroute.hussar.service.sql.MySQLDockerService;

import static pl.netroute.hussar.junit5.assertion.ApplicationAssertionHelper.assertApplicationBootstrapped;
import static pl.netroute.hussar.junit5.assertion.MySQLAssertionHelper.assertMySQLBootstrapped;

@ExtendWith(HussarJUnit5Extension.class)
@HussarEnvironment(configurerProvider = ModuleTestEnvironmentConfigurerProvider.class)
public class HussarModuleJUnit5IT {

    @HussarApplication
    private Application application;

    @HussarService
    private MySQLDockerService mySQLService;

    @Test
    public void shouldStartupEnvironment() {
        // given
        // when
        // then
        assertApplicationBootstrapped(application);
        assertMySQLBootstrapped(mySQLService);
    }

}