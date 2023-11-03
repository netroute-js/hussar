package pl.netroute.hussar.core.service;

import pl.netroute.hussar.core.helper.ValidatorHelper;

public abstract class BaseServiceConfig {
    private final String name;

    public BaseServiceConfig(String name) {
        ValidatorHelper.requireNonEmpty(name, "name is required");

        this.name = name;
    }

    public String getName() {
        return name;
    }
}
