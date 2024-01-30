package pl.netroute.hussar.core.service.resolver;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.Optional;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ServiceNameResolver {
    private static final String DEFAULT_SERVICE_NAME_TEMPLATE = "%s_%s";

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
