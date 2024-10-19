package pl.netroute.hussar.core.service.assertion;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import pl.netroute.hussar.core.api.Endpoint;
import pl.netroute.hussar.core.api.configuration.ConfigurationEntry;
import pl.netroute.hussar.core.api.service.Service;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ServiceAssertionHelper {

    public static void assertName(@NonNull Service service,
                                  @NonNull String expectedName) {
        assertThat(service.getName()).isEqualTo(expectedName);
    }

    public static void assertSingleEndpoint(@NonNull Service service,
                                            @NonNull Endpoint expectedEndpoint) {
        assertThat(service.getEndpoints()).containsExactly(expectedEndpoint);
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

    // assert name
}
