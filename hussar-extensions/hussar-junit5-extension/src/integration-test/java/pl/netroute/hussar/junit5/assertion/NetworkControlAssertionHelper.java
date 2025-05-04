package pl.netroute.hussar.junit5.assertion;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import pl.netroute.hussar.core.service.api.Service;

import static org.assertj.core.api.Assertions.assertThat;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NetworkControlAssertionHelper {

    public static void assertNetworkControlConfigured(@NonNull Service service) {
        assertThat(service.getNetworkControl()).isNotNull();
    }

}
