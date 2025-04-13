package pl.netroute.hussar.core.network;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import pl.netroute.hussar.core.api.Endpoint;
import pl.netroute.hussar.core.api.InternalUseOnly;
import pl.netroute.hussar.core.network.api.Network;
import pl.netroute.hussar.core.network.api.NetworkConfigurer;

import java.util.List;

@InternalUseOnly
@NoArgsConstructor(access = AccessLevel.PACKAGE)
class LazyNetworkConfigurer implements NetworkConfigurer {

    @Setter(AccessLevel.PACKAGE)
    private NetworkConfigurer networkConfigurer;

    @Override
    public Network configure(@NonNull String networkPrefix, @NonNull List<Endpoint> endpoints) {
        if(networkConfigurer == null) {
            throw new IllegalStateException("Expected NetworkConfigurer to be initialized");
        }

        return networkConfigurer.configure(networkPrefix, endpoints);
    }

    boolean isInitialized() {
        return networkConfigurer != null;
    }

}
