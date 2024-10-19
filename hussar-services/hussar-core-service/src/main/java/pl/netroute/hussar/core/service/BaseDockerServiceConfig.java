package pl.netroute.hussar.core.service;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

/**
 * A base configuration of Hussar Docker {@link pl.netroute.hussar.core.api.Service}.
 */
@Getter
@SuperBuilder
public abstract class BaseDockerServiceConfig extends BaseServiceConfig {

    @NonNull
    private final String dockerImage;

    private final String scheme;
}
