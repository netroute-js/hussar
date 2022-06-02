package pl.netroute.hussar.core.api;

import pl.netroute.hussar.core.Endpoint;

import java.util.List;

public interface Accessible {
    List<Endpoint> getEndpoints();
}
