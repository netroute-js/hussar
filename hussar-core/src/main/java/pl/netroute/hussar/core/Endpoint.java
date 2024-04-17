package pl.netroute.hussar.core;

import lombok.NonNull;
import pl.netroute.hussar.core.helper.SchemesHelper;

public record Endpoint(@NonNull String scheme,
                       @NonNull String host,
                       int port) {
    private static final String ADDRESS_FORMAT = "%s%s:%d";

    public Endpoint {
        if(port <= 0) {
            throw new IllegalArgumentException("Expected a valid port");
        }
    }

    public String address() {
        return ADDRESS_FORMAT.formatted(scheme, host, port);
    }

    public static Endpoint schemeLess(String host,
                                      int port) {
        return new Endpoint(SchemesHelper.EMPTY_SCHEME, host, port);
    }

    public static Endpoint of(String scheme,
                              String host,
                              int port) {
        return new Endpoint(scheme, host, port);
    }

}
