package pl.netroute.hussar.core.service;

import pl.netroute.hussar.core.api.Service;
import pl.netroute.hussar.core.helper.ValidatorHelper;

public abstract class BaseServiceConfigurer<S extends Service, C extends BaseServiceConfigurer<S, C>> {
    private String name;

    public C name(String name) {
        ValidatorHelper.requireNonEmpty(name, "name");

        this.name = name;

        return (C) this;
    }

    protected String resolveName(String service) {
        ValidatorHelper.requireNonEmpty(service, "service is required");

        return ServiceNameResolver.resolve(service, getName());
    }

    protected String getName() {
        return name;
    }

    protected abstract S configure();
}
