package pl.netroute.hussar.junit5.assertion;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.netroute.hussar.core.network.api.NetworkRestore;

import static org.assertj.core.api.Assertions.assertThat;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NetworkRestoreAssertionHelper {

    public static void assertNetworkRestoreInjected(NetworkRestore networkRestore) {
        assertThat(networkRestore).isNotNull();
    }

}
