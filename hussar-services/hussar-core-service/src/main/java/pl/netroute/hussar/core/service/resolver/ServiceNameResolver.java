package pl.netroute.hussar.core.service.resolver;

import pl.netroute.hussar.core.helper.ValidatorHelper;

import java.util.Optional;
import java.util.UUID;

public class ServiceNameResolver {
    private static final String DEFAULT_SERVICE_NAME_TEMPLATE = "%s_%s";

    private ServiceNameResolver() {}

    public static String resolve(String service, String name) {
        ValidatorHelper.requireNonEmpty(service, "service is required");

        return Optional
                .ofNullable(name)
                .filter(actualName -> !actualName.isBlank())
                .orElseGet(() -> resolveDefaultName(service));
    }

    private static String resolveDefaultName(String service) {
        var uniqueName = UUID.randomUUID().toString();

        return String.format(DEFAULT_SERVICE_NAME_TEMPLATE, service, uniqueName);
    }

}
