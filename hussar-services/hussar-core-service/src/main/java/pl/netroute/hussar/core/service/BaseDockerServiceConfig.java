package pl.netroute.hussar.core.service;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter(AccessLevel.PACKAGE)
public abstract class BaseDockerServiceConfig extends BaseServiceConfig {

    @NonNull
    private final String dockerImage;

    private final String scheme;
}
