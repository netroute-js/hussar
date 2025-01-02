package pl.netroute.hussar.core.service;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import pl.netroute.hussar.core.service.api.Service;

/**
 * A base configuration of Hussar Docker {@link Service}.
 */
@Getter
@SuperBuilder
public abstract class BaseDockerServiceConfig extends BaseServiceConfig {

    @NonNull
    private final String dockerImage;

    private final String scheme;
}
