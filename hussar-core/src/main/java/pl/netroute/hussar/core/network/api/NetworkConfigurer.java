package pl.netroute.hussar.core.network.api;

import lombok.NonNull;
import pl.netroute.hussar.core.api.Endpoint;

import java.util.List;

public interface NetworkConfigurer {
    Network configure(@NonNull String networkPrefix, @NonNull List<Endpoint> endpoints);
}
