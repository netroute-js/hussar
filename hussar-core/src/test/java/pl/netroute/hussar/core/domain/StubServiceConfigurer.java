package pl.netroute.hussar.core.domain;

import lombok.Getter;
import lombok.NonNull;
import pl.netroute.hussar.core.service.api.Service;
import pl.netroute.hussar.core.service.api.ServiceConfigureContext;
import pl.netroute.hussar.core.service.api.ServiceConfigurer;

import static org.mockito.Mockito.mock;

public class StubServiceConfigurer<S extends Service> implements ServiceConfigurer<S> {

    @Getter
    @NonNull
    private final S service;

    public StubServiceConfigurer(@NonNull Class<S> serviceType) {
        this.service = mock(serviceType);
    }

    @Override
    public S configure(@NonNull ServiceConfigureContext context) {
        return service;
    }

}
