package pl.netroute.hussar.core.service.assertion;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import pl.netroute.hussar.core.configuration.api.ConfigurationEntry;
import pl.netroute.hussar.core.network.api.Network;
import pl.netroute.hussar.core.service.api.Service;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ServiceAssertionHelper {

    public static void assertName(@NonNull Service service,
                                  @NonNull String expectedName) {
        assertThat(service.getName()).isEqualTo(expectedName);
    }

    public static void assertEndpoints(@NonNull Service service,
                                       @NonNull Network network) {
        var actualEndpoints = service.getEndpoints();
        var expectedEndpoints = network.getEndpoints();

        assertThat(actualEndpoints).containsExactlyInAnyOrderElementsOf(expectedEndpoints);
    }

    public static void assertDirectEndpoints(@NonNull Service service,
                                             @NonNull Network network) {
        var actualDirectEndpoints = service.getDirectEndpoints();
        var expectedDirectEndpoints = network.getEndpoints();

        assertThat(actualDirectEndpoints).containsExactlyInAnyOrderElementsOf(expectedDirectEndpoints);
    }

    public static void assertNetworkControl(@NonNull Service service) {
        assertThat(service.getNetworkControl()).isNotNull();
    }

    public static void assertEntriesRegistered(@NonNull Service service,
                                               @NonNull List<ConfigurationEntry> expectedEntries) {
        var registry = service.getConfigurationRegistry();

        assertThat(registry.getEntries()).containsExactlyInAnyOrderElementsOf(expectedEntries);
    }

    public static void assertNoEntriesRegistered(@NonNull Service service) {
        var registry = service.getConfigurationRegistry();

        assertThat(registry.getEntries()).isEmpty();
    }

}
