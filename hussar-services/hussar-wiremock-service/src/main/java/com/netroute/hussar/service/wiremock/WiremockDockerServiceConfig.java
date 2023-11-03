package com.netroute.hussar.service.wiremock;

import pl.netroute.hussar.core.service.BaseDockerServiceConfig;
import pl.netroute.hussar.core.service.api.RegistrableConfigurationEntry;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

class WiremockDockerServiceConfig extends BaseDockerServiceConfig {

    private final Set<RegistrableConfigurationEntry> registerEndpointUnderEntries;

    WiremockDockerServiceConfig(String name,
                                String dockerImage,
                                Optional<String> scheme,
                                Set<RegistrableConfigurationEntry> registerEndpointUnderEntries) {
        super(name, dockerImage, scheme);

        Objects.requireNonNull(registerEndpointUnderEntries, "registerEndpointUnderEntries is required");

        this.registerEndpointUnderEntries = registerEndpointUnderEntries;
    }

    Set<RegistrableConfigurationEntry> getRegisterEndpointUnderEntries() {
        return registerEndpointUnderEntries;
    }
}
