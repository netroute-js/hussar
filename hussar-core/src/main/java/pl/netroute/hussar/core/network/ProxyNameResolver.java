package pl.netroute.hussar.core.network;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import pl.netroute.hussar.core.api.InternalUseOnly;

import java.util.UUID;

@InternalUseOnly
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class ProxyNameResolver {
    private static final String PROXY_NAME_TEMPLATE = "%s-%s";

    static String resolve(@NonNull String proxyPrefix) {
        var proxySuffix = UUID.randomUUID().toString();

        return String.format(PROXY_NAME_TEMPLATE, proxyPrefix, proxySuffix);
    }

}
