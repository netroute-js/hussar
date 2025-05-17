package pl.netroute.hussar.junit5;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import pl.netroute.hussar.core.environment.api.HussarEnvironment;
import pl.netroute.hussar.core.network.api.HussarNetworkRestore;
import pl.netroute.hussar.core.network.api.NetworkRestore;
import pl.netroute.hussar.core.service.api.HussarService;
import pl.netroute.hussar.junit5.api.HussarJUnit5Extension;
import pl.netroute.hussar.junit5.config.ModuleTestEnvironmentConfigurerProvider;
import pl.netroute.hussar.service.sql.api.MySQLDockerService;

import static pl.netroute.hussar.junit5.assertion.MySQLAssertionHelper.assertMySQLBootstrapped;

@ExtendWith(HussarJUnit5Extension.class)
@HussarEnvironment(configurerProvider = ModuleTestEnvironmentConfigurerProvider.class)
public class HussarNetworkRestoreJUnitIT {

    @HussarService
    private MySQLDockerService mySQLService;

    @HussarNetworkRestore
    private NetworkRestore networkRestore;

    @Test
    public void shouldRestoreNetwork() {
        // given
        var networkControl = mySQLService.getNetworkControl();

        // when
        networkControl.disable();
        networkRestore.restoreToDefault();

        // then
        assertMySQLBootstrapped(mySQLService);
    }

}
