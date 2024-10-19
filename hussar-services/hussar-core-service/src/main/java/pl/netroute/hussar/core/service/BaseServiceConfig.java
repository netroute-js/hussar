package pl.netroute.hussar.core.service;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

import java.util.Set;

/**
 * A base configuration of Hussar {@link pl.netroute.hussar.core.api.Service}.
 */
@Getter
@SuperBuilder
public abstract class BaseServiceConfig {

    @NonNull
    private final String name;

    @NonNull
    private final Set<String> registerEndpointUnderProperties;

    @NonNull
    private final Set<String> registerEndpointUnderEnvironmentVariables;

}
