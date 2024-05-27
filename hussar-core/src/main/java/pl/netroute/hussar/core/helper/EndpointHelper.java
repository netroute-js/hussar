package pl.netroute.hussar.core.helper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import pl.netroute.hussar.core.api.Endpoint;
import pl.netroute.hussar.core.api.Service;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EndpointHelper {

    public static Endpoint getAnyEndpointOrFail(@NonNull Service service) {
        return service
                .getEndpoints()
                .stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Expected at least one Endpoint for a given Service: " + service.getName()));
    }

}
