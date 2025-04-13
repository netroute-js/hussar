package pl.netroute.hussar.core.network;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import pl.netroute.hussar.core.api.Endpoint;
import pl.netroute.hussar.core.api.InternalUseOnly;
import pl.netroute.hussar.core.network.api.Network;
import pl.netroute.hussar.core.network.api.NetworkControl;

import java.util.List;

@InternalUseOnly
@RequiredArgsConstructor
public class DefaultNetwork implements Network {

    @Getter
    @NonNull
    private final List<Endpoint> endpoints;

    @Getter
    @NonNull
    private final NetworkControl networkControl;

}
