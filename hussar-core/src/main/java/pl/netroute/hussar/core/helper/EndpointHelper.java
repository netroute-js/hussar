package pl.netroute.hussar.core.helper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import pl.netroute.hussar.core.api.InternalUseOnly;
import pl.netroute.hussar.core.application.api.Application;
import pl.netroute.hussar.core.api.Endpoint;
import pl.netroute.hussar.core.service.api.Service;

/**
 * An {@link Endpoint} helper.
 */
@InternalUseOnly
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EndpointHelper {

    /**
     * It extracts the first available {@link Endpoint} of a given {@link Application} or fails fast.
     *
     * @param application - the {@link Application}.
     * @return the first available {@link Endpoint}.
     */
    public static Endpoint getAnyEndpointOrFail(@NonNull Application application) {
        return application
                .getEndpoints()
                .stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Expected at least one Endpoint for a given Application: %s".formatted(application.getClass())));
    }

    /**
     * It extracts the first available {@link Endpoint} of a given {@link Service} or fails fast.
     *
     * @param service - the {@link Service}.
     * @return the first available {@link Endpoint}.
     */
    public static Endpoint getAnyEndpointOrFail(@NonNull Service service) {
        return service
                .getEndpoints()
                .stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Expected at least one Endpoint for a given Service: %s".formatted(service.getName())));
    }

    /**
     * It extracts the first available direct {@link Endpoint} of a given {@link Service} or fails fast.
     *
     * @param service - the {@link Service}.
     * @return the first available {@link Endpoint}.
     */
    public static Endpoint getAnyDirectEndpointOrFail(@NonNull Service service) {
        return service
                .getDirectEndpoints()
                .stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Expected at least one direct Endpoint for a given Service: %s".formatted(service.getName())));
    }

}
