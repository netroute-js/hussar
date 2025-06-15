package pl.netroute.hussar.core.service.api;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import pl.netroute.hussar.core.api.InternalUseOnly;

import java.time.Duration;

/**
 * A base configuration of Hussar Docker {@link Service}.
 */
@Getter
@SuperBuilder
@InternalUseOnly
public abstract class BaseDockerServiceConfig extends BaseServiceConfig {

    @NonNull
    private final String dockerImage;

    @NonNull
    private final Duration startupTimeout;

    private final String scheme;

}
