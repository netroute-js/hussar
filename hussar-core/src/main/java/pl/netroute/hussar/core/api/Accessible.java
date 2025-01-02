package pl.netroute.hussar.core.api;

import java.util.List;

/**
 * Hussar interface responsible for exposing methods to access component.
 */
public interface Accessible {

    /**
     * Returns endpoints of component.
     *
     * @return the list of component's endpoints.
     */
    List<Endpoint> getEndpoints();

}
