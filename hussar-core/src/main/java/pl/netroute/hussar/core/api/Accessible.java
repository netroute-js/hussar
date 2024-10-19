package pl.netroute.hussar.core.api;

import java.util.List;

/**
 * Hussar interface responsible for exposing methods to access {@link Application} or {@link Service}.
 */
public interface Accessible {

    /**
     * Returns endpoints of {@link Application} or {@link Service}.
     *
     * @return the list of endpoints of {@link Application} or {@link Service}.
     */
    List<Endpoint> getEndpoints();

}
