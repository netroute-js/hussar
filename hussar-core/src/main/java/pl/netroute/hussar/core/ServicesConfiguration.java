package pl.netroute.hussar.core;

import pl.netroute.hussar.core.api.Service;

import java.util.List;

class ServicesConfiguration {
    private final List<Service> standaloneServices;

    ServicesConfiguration(List<Service> standaloneServices) {
        this.standaloneServices = standaloneServices;
    }

    List<Service> getStandaloneServices() {
        return standaloneServices;
    }

}
