package pl.netroute.hussar.core.service;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Singular;
import lombok.experimental.SuperBuilder;
import pl.netroute.hussar.core.api.Service;

import java.util.Set;

@Getter(AccessLevel.PROTECTED)
@SuperBuilder(builderMethodName = "newInstance", buildMethodName = "done")
public abstract class BaseServiceConfigurer<S extends Service> {

    private final String name;

    @Singular
    private Set<String> registerEndpointUnderProperties;

    @Singular
    private Set<String> registerEndpointUnderEnvironmentVariables;

    protected abstract S configure();

}
