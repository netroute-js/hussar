package pl.netroute.hussar.core.service;

import lombok.Builder;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import pl.netroute.hussar.core.api.Service;

@SuperBuilder(builderMethodName = "newInstance", buildMethodName = "done")
public abstract class BaseDockerServiceConfigurer<S extends Service> extends BaseServiceConfigurer<S> {

    @NonNull
    protected final @Builder.Default String dockerImageVersion = "latest";

}
