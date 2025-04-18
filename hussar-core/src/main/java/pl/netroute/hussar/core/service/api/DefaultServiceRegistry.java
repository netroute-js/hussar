package pl.netroute.hussar.core.service.api;

import lombok.NonNull;
import pl.netroute.hussar.core.helper.StringHelper;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * An actual implementation of {@link ServiceRegistry}.
 */
public class DefaultServiceRegistry implements ServiceRegistry {

    @NonNull
    private final Set<Service> registeredServices;

    /**
     * Creates new instance of {@link DefaultServiceRegistry}.
     */
    public DefaultServiceRegistry() {
        this.registeredServices = new HashSet<>();
    }

    @Override
    public void register(@NonNull Service service) {
        validateIfServiceCanBeRegistered(service);

        registeredServices.add(service);
    }

    @Override
    public Set<Service> getEntries() {
        return Set.copyOf(registeredServices);
    }

    @Override
    public Optional<Service> findEntryByName(@NonNull String name) {
        return registeredServices
                .stream()
                .filter(service -> name.equals(service.getName()))
                .findFirst();
    }

    @Override
    public Optional<Service> findEntryByType(@NonNull Class<? extends Service> type) {
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

    public static DefaultServiceRegistry of(@NonNull Set<Service> services) {
        var serviceRegistry = new DefaultServiceRegistry();
        services.forEach(serviceRegistry::register);

        return serviceRegistry;
    }

    public static DefaultServiceRegistry of(@NonNull Service... services) {
        var servicesSet = Stream
                .of(services)
                .collect(Collectors.toUnmodifiableSet());

        return of(servicesSet);
    }

}
