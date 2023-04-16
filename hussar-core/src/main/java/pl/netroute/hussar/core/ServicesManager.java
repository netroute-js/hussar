package pl.netroute.hussar.core;

import pl.netroute.hussar.core.api.Service;
import pl.netroute.hussar.core.helper.ValidatorHelper;

import java.util.Objects;
import java.util.Optional;

class ServicesManager {
    private final ServicesConfiguration services;

    ServicesManager(ServicesConfiguration services) {
        Objects.requireNonNull(services, "services is required");

        this.services = services;
    }

    Optional<Service> findByName(String name) {
        ValidatorHelper.requireNonEmpty(name, "name is required");

        return services
                .getStandaloneServices()
                .stream()
                .filter(service -> name.equals(service.getName()))
                .findFirst();
    }

    Optional<Service> findByType(Class<? extends Service> type) {
        Objects.requireNonNull(type, "type is required");

        return services
                .getStandaloneServices()
                .stream()
                .filter(service -> type.equals(service.getClass()))
                .findFirst();
    }

}
