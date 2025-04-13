package pl.netroute.hussar.core.test.stub;

import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import pl.netroute.hussar.core.service.ServiceConfigureContext;
import pl.netroute.hussar.core.service.api.Service;
import pl.netroute.hussar.core.service.api.ServiceConfigurer;

@SuperBuilder(builderMethodName = "newInstance", buildMethodName = "done")
class BaseServiceConfigurerStub<S extends Service> implements ServiceConfigurer<S> {
    private final S service;

    @Override
    public S configure(@NonNull ServiceConfigureContext context) {
        return service;
    }

}
