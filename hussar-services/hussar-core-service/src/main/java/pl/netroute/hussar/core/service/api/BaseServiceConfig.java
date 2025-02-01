package pl.netroute.hussar.core.service.api;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import pl.netroute.hussar.core.api.InternalUseOnly;

import java.util.Set;

/**
 * A base configuration of Hussar {@link Service}.
 */
@Getter
@SuperBuilder
@InternalUseOnly
public abstract class BaseServiceConfig {

    @NonNull
    private final String name;

    @NonNull
    private final Set<String> registerEndpointUnderProperties;

    @NonNull
    private final Set<String> registerEndpointUnderEnvironmentVariables;

}
