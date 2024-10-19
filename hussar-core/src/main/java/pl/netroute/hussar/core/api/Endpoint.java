package pl.netroute.hussar.core.api;

import lombok.NonNull;
import pl.netroute.hussar.core.helper.SchemesHelper;

/**
 * Custom type that represents a physical endpoint. It's fully configurable so can be used with many schemas etc.
 */
public record Endpoint(@NonNull String scheme,
                       @NonNull String host,
                       int port) {
    private static final String ADDRESS_FORMAT = "%s%s:%d";

    /**
     * Creates new instance of {@link Endpoint}.
     *
     * @param scheme the scheme
     * @param host   the host
     * @param port   the port
     */
    public Endpoint {
        if(port <= 0) {
            throw new IllegalArgumentException("Expected a valid port");
        }
    }

    /**
     * Returns formatted address with schema, host and port.
     *
     * @return the formatted address.
     */
    public String address() {
        return ADDRESS_FORMAT.formatted(scheme, host, port);
    }

    /**
     * Factory method to create {@link Endpoint} without schema.
     *
     * @param host - the host of the endpoint.
     * @param port - the port of the endpoint.
     * @return schemaless instance of {@link Endpoint}.
     */
    public static Endpoint schemeLess(@NonNull String host,
                                      int port) {
        return new Endpoint(SchemesHelper.EMPTY_SCHEME, host, port);
    }

    /**
     * Factory method to create {@link Endpoint}.
     *
     * @param scheme - the schema of the endpoint.
     * @param host - the host of the endpoint.
     * @param port - the port of the endpoint.
     * @return an instance of {@link Endpoint}.
     */
    public static Endpoint of(String scheme,
                              String host,
                              int port) {
        return new Endpoint(scheme, host, port);
    }

}
