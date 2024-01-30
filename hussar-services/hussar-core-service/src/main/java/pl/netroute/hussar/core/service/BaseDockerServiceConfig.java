package pl.netroute.hussar.core.service;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public abstract class BaseDockerServiceConfig extends BaseServiceConfig {

    @NonNull
    private final String dockerImage;

    private final String scheme;
}
