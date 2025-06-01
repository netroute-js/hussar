package pl.netroute.hussar.core.network;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import pl.netroute.hussar.core.api.InternalUseOnly;
import pl.netroute.hussar.core.helper.UUIDHelper;

import java.util.UUID;

@InternalUseOnly
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class ProxyNameResolver {
    private static final String PROXY_NAME_TEMPLATE = "%s-%s";

    static String resolve(@NonNull String proxyPrefix) {
        var proxySuffix = UUIDHelper.extractFirstPart(UUID.randomUUID());

        return String.format(PROXY_NAME_TEMPLATE, proxyPrefix, proxySuffix);
    }

}
