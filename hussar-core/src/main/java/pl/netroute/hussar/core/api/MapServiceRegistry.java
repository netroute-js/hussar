package pl.netroute.hussar.core.api;

import pl.netroute.hussar.core.helper.StringHelper;
import pl.netroute.hussar.core.helper.ValidatorHelper;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class MapServiceRegistry implements ServiceRegistry {
    private final Set<Service> registeredServices;

    public MapServiceRegistry() {
        this(Set.of());
    }

    public MapServiceRegistry(Set<Service> services) {
        Objects.requireNonNull(services, "services is required");

        this.registeredServices = new HashSet<>(services);
    }

    @Override
    public void register(Service service) {
        Objects.requireNonNull(service, "service is required");

        validateIfServiceCanBeRegistered(service);

        registeredServices.add(service);
    }

    @Override
    public Set<Service> getEntries() {
        return Set.copyOf(registeredServices);
    }

    @Override
    public Optional<Service> findEntryByName(String name) {
        ValidatorHelper.requireNonEmpty(name, "name is required");

        return registeredServices
                .stream()
                .filter(service -> name.equals(service.getName()))
                .findFirst();
    }

    @Override
    public Optional<Service> findEntryByType(Class<? extends Service> type) {
        Objects.requireNonNull(type, "type is required");

        return registeredServices
                .stream()
                .filter(service -> type.equals(service.getClass()))
                .findFirst();
    }

    private boolean isTypedService(Service service) {
        return !StringHelper.hasValue(service.getName());
    }

    private boolean isNamedService(Service service) {
        return StringHelper.hasValue(service.getName());
    }

    private void validateIfServiceCanBeRegistered(Service service) {
        validateIfTypedServiceCanBeRegistered(service);
        validateIfNamedServiceCanBeRegistered(service);
    }

    private void validateIfTypedServiceCanBeRegistered(Service service) {
        if(isTypedService(service)) {
            var serviceType = service.getClass();

            registeredServices
                    .stream()
                    .filter(this::isTypedService)
                    .filter(actualService -> actualService.getClass().equals(serviceType))
                    .findFirst()
                    .ifPresent(alreadyRegisteredService -> {
                        var errorMessage = String.format("Could not register typed service - %s. If you want more services of the same type registered then all of them need to be named", serviceType.getCanonicalName());

                        throw new IllegalArgumentException(errorMessage);
                    });
        }
    }

    private void validateIfNamedServiceCanBeRegistered(Service service) {
        if(isNamedService(service)) {
            var serviceName = service.getName();

            registeredServices
                    .stream()
                    .filter(this::isNamedService)
                    .filter(actualService -> actualService.getName().equals(serviceName))
                    .findFirst()
                    .ifPresent(alreadyRegisteredService -> {
                        var errorMessage = String.format("Could not register named service - %s. There is already a service registered with that name. Service name must be unique", serviceName);

                        throw new IllegalArgumentException(errorMessage);
                    });
        }

    }
}
