package pl.netroute.hussar.core.service;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import pl.netroute.hussar.core.api.Service;

@Getter(AccessLevel.PROTECTED)
@SuperBuilder(builderMethodName = "newInstance", buildMethodName = "done")
public abstract class BaseDockerServiceConfigurer<S extends Service> extends BaseServiceConfigurer<S> {

    @NonNull
    private final @Builder.Default String dockerImageVersion = "latest";

}
