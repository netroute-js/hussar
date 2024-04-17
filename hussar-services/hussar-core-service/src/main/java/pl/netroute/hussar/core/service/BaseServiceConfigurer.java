package pl.netroute.hussar.core.service;

import lombok.NonNull;
import lombok.Singular;
import lombok.experimental.SuperBuilder;
import pl.netroute.hussar.core.api.Service;

import java.util.Set;

@SuperBuilder(builderMethodName = "newInstance", buildMethodName = "done")
public abstract class BaseServiceConfigurer<S extends Service> {

    protected final String name;

    @NonNull
    @Singular
    protected final Set<String> registerEndpointUnderProperties;

    @NonNull
    @Singular
    protected final Set<String> registerEndpointUnderEnvironmentVariables;

    protected abstract S configure();

}
