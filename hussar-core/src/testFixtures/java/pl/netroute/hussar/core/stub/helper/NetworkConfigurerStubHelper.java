package pl.netroute.hussar.core.stub.helper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import pl.netroute.hussar.core.api.Endpoint;
import pl.netroute.hussar.core.network.api.NetworkConfigurer;
import pl.netroute.hussar.core.stub.NetworkStub;

import java.util.List;

import static org.mockito.Mockito.when;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NetworkConfigurerStubHelper {

    public static void givenNetworkConfigured(@NonNull NetworkConfigurer networkConfigurer,
                                              @NonNull String networkPrefix,
                                              @NonNull Endpoint... endpoints) {
        givenNetworkConfigured(networkConfigurer, networkPrefix, List.of(endpoints));
    }

    public static void givenNetworkConfigured(@NonNull NetworkConfigurer networkConfigurer,
                                              @NonNull String networkPrefix,
                                              @NonNull List<Endpoint> endpoints) {
        var network = NetworkStub.newStub(endpoints);

        when(networkConfigurer.configure(networkPrefix, endpoints)).thenReturn(network);
    }

}
