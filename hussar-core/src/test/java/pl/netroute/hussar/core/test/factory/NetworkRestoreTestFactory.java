package pl.netroute.hussar.core.test.factory;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import pl.netroute.hussar.core.network.DefaultNetworkRestore;
import pl.netroute.hussar.core.network.api.NetworkRestore;
import pl.netroute.hussar.core.service.api.Service;
import pl.netroute.hussar.core.service.api.ServiceRegistry;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NetworkRestoreTestFactory {

    public static NetworkRestore create(@NonNull ServiceRegistry serviceRegistry) {
        var networkControls = serviceRegistry
                .getEntries()
                .stream()
                .map(Service::getNetworkControl)
                .toList();

        return new DefaultNetworkRestore(networkControls);
    }

}
