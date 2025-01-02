package pl.netroute.hussar.core.service;

import pl.netroute.hussar.core.service.api.Service;

/**
 * Hussar interface responsible for configuring {@link Service}.
 *
 * @param <S> the type of the {@link Service} to be configured.
 */
public interface ServiceConfigurer<S extends Service> {

    /**
     * Configure an instance of {@link Service}.
     *
     * @return the instance of configured {@link Service}
     */
    S configure();

}
