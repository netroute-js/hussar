package pl.netroute.hussar.core;

import java.util.Objects;

public class Endpoint {
    private static final String EMPTY_SCHEME = "";
    private static final String ADDRESS_FORMAT = "%s%s:%d";

    private final String address;
    private final String host;
    private final int port;

    private Endpoint(String scheme,
                     String host,
                     int port) {
        Objects.requireNonNull(scheme, "scheme cannot be null");
        Objects.requireNonNull(host, "host cannot be null");

        if(port <= 0) {
            throw new IllegalArgumentException("Expected a valid port");
        }

        this.host = host;
        this.port = port;
        this.address = String.format(ADDRESS_FORMAT, scheme, host, port);
    }

    public String getAddress() {
        return address;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public static Endpoint of(String host,
                              int port) {
        return new Endpoint(EMPTY_SCHEME, host, port);
    }

    public static Endpoint of(String scheme,
                              String host,
                              int port) {
        return new Endpoint(scheme, host, port);
    }

}
