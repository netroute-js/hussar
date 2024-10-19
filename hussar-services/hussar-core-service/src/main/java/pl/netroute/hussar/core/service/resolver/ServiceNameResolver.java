package pl.netroute.hussar.core.service.resolver;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.Optional;
import java.util.UUID;

/**
 * A custom {@link pl.netroute.hussar.core.api.Service} name resolver. It's crucial that all the {@link pl.netroute.hussar.core.api.Service} have unique names.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ServiceNameResolver {
    private static final String DEFAULT_SERVICE_NAME_TEMPLATE = "%s_%s";

    /**
     * Resolve a unique {@link pl.netroute.hussar.core.api.Service} name.
     *
     * @param service - the {@link pl.netroute.hussar.core.api.Service} prefix.
     * @param name - the possible {@link pl.netroute.hussar.core.api.Service} name. Can be null. In such case the random name will be generated.
     * @return the unique name of the {@link pl.netroute.hussar.core.api.Service}.
     */
    public static String resolve(@NonNull String service,
                                 String name) {
        return Optional
                .ofNullable(name)
                .filter(actualName -> !actualName.isBlank())
                .orElseGet(() -> resolveDefaultName(service));
    }

    private static String resolveDefaultName(String service) {
        var uniqueName = UUID.randomUUID().toString();

        return DEFAULT_SERVICE_NAME_TEMPLATE.formatted(service, uniqueName);
    }

}
