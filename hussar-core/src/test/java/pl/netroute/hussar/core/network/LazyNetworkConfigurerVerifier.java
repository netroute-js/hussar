package pl.netroute.hussar.core.network;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import pl.netroute.hussar.core.api.Endpoint;
import pl.netroute.hussar.core.network.api.Network;
import pl.netroute.hussar.core.network.api.NetworkConfigurer;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class LazyNetworkConfigurerVerifier {
    private final NetworkConfigurer networkConfigurer;

    void verifyNetworkConfigured(@NonNull Network network,
                                 @NonNull String networkPrefix,
                                 @NonNull List<Endpoint> internalEndpoints) {
        assertThat(network).isNotNull();

        verify(networkConfigurer).configure(networkPrefix, internalEndpoints);
    }

}