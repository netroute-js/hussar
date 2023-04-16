package pl.netroute.hussar.core;

import pl.netroute.hussar.core.api.Service;
import pl.netroute.hussar.core.helper.StringHelper;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

class ServicesConfiguration {
    private final List<Service> standaloneServices;

    ServicesConfiguration(List<Service> standaloneServices) {
        Objects.requireNonNull(standaloneServices, "standaloneServices is required");

        validateServices(standaloneServices);

        this.standaloneServices = standaloneServices;
    }

    List<Service> getStandaloneServices() {
        return standaloneServices;
    }

    private void validateMultipleTypedServices(List<Service> services) {
        var invalidServices = services
                .stream()
                .collect(Collectors.groupingBy(Service::getClass))
                .values()
                .stream()
                .filter(groupedServices -> groupedServices.size() > 1)
                .filter(groupedServices -> groupedServices
                        .stream()
                        .anyMatch(service -> !StringHelper.hasValue(service.getName())))
                .map(groupedServices -> groupedServices.get(0))
                .map(Service::getClass)
                .map(Class::getCanonicalName)
                .collect(Collectors.toUnmodifiableList());

        if(!invalidServices.isEmpty()) {
            var errorMessage = String.format("Multiple services of the same type detected. Expected all of them to be named - %s", invalidServices);

            throw new IllegalArgumentException(errorMessage);
        }
    }

    private void validateNamedServices(List<Service> services) {
        var invalidServices = services
                .stream()
                .filter(service -> StringHelper.hasValue(service.getName()))
                .collect(Collectors.groupingBy(Service::getName))
                .values()
                .stream()
                .filter(groupedServices -> groupedServices.size() > 1)
                .map(groupedServices -> groupedServices.get(0))
                .map(Service::getName)
                .collect(Collectors.toUnmodifiableList());

        if(!invalidServices.isEmpty()) {
            var errorMessage = String.format("Multiple services of the same name detected. Expected all of them to uniquely named - %s", invalidServices);

            throw new IllegalArgumentException(errorMessage);
        }

    }

    private void validateServices(List<Service> services) {
        validateMultipleTypedServices(services);
        validateNamedServices(services);
    }
}
