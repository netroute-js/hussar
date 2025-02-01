package pl.netroute.hussar.core.service;

import pl.netroute.hussar.core.api.InternalUseOnly;
import pl.netroute.hussar.core.service.api.Service;

/**
 * Hussar context used during {@link Service} startup.
 */
@InternalUseOnly
public record ServiceStartupContext() {

    /**
     * Returns default context.
     *
     * @return the actual default context.
     */
    public static ServiceStartupContext defaultContext() {
        return new ServiceStartupContext();
    }

}
