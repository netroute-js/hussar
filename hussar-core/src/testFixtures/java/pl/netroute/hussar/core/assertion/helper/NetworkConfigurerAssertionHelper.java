package pl.netroute.hussar.core.assertion.helper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import pl.netroute.hussar.core.api.Endpoint;
import pl.netroute.hussar.core.network.api.NetworkConfigurer;

import java.util.List;

import static org.mockito.Mockito.verify;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NetworkConfigurerAssertionHelper {

    public static void assertNetworkConfigured(@NonNull NetworkConfigurer networkConfigurer,
                                               @NonNull String networkPrefix,
                                               @NonNull Endpoint... endpoints) {
        assertNetworkConfigured(networkConfigurer, networkPrefix, List.of(endpoints));
    }

    public static void assertNetworkConfigured(@NonNull NetworkConfigurer networkConfigurer,
                                               @NonNull String networkPrefix,
                                               @NonNull List<Endpoint> endpoints) {
        verify(networkConfigurer).configure(networkPrefix, endpoints);
    }

}
