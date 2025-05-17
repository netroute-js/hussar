package pl.netroute.hussar.core.network;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import pl.netroute.hussar.core.api.InternalUseOnly;
import pl.netroute.hussar.core.network.api.NetworkControl;
import pl.netroute.hussar.core.network.api.NetworkRestore;

import java.util.List;

@InternalUseOnly
@EqualsAndHashCode
@RequiredArgsConstructor
public class DefaultNetworkRestore implements NetworkRestore {
    private final List<NetworkControl> networkControls;

    @Override
    public void restoreToDefault() {
        networkControls.forEach(NetworkControl::reset);
    }

}
