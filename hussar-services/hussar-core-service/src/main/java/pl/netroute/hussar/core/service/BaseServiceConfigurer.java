package pl.netroute.hussar.core.service;

import lombok.NonNull;
import lombok.Singular;
import lombok.experimental.SuperBuilder;
import pl.netroute.hussar.core.api.service.Service;

import java.util.Set;

/**
 * A base configurer class for all {@link Service}. The configurer is responsible for configuring {@link Service}.
 *
 * @param <S> the type of the {@link Service} to be configured.
 */
@SuperBuilder(builderMethodName = "newInstance", buildMethodName = "done")
public abstract class BaseServiceConfigurer<S extends Service> implements ServiceConfigurer<S> {

    /**
     * The Name of the {@link Service}.
     */
    protected final String name;

    /**
     * Set of properties to be used to register {@link pl.netroute.hussar.core.api.Endpoint} under.
     */
    @NonNull
    @Singular
    protected final Set<String> registerEndpointUnderProperties;

    /**
     * Set of environment variables to be used to register {@link pl.netroute.hussar.core.api.Endpoint} under.
     */
    @NonNull
    @Singular
    protected final Set<String> registerEndpointUnderEnvironmentVariables;

}
