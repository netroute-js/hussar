package com.netroute.hussar.wiremock;

import pl.netroute.hussar.core.helper.ValidatorHelper;
import pl.netroute.hussar.core.service.api.RegistrableConfigurationEntry;

import java.util.Objects;
import java.util.Set;

class WiremockServiceConfig {
    private final String name;
    private final String dockerImage;
    private final Set<RegistrableConfigurationEntry> registerEndpointUnderEntries;

    WiremockServiceConfig(String name, String dockerImage, Set<RegistrableConfigurationEntry> registerEndpointUnderEntries) {
        ValidatorHelper.requireNonEmpty(name, "name is required");
        ValidatorHelper.requireNonEmpty(dockerImage, "dockerImage is required");
        Objects.requireNonNull(registerEndpointUnderEntries, "registerEndpointUnderEntries is required");

        this.name = name;
        this.dockerImage = dockerImage;
        this.registerEndpointUnderEntries = registerEndpointUnderEntries;
    }

    String getName() {
        return name;
    }

    String getDockerImage() {
        return dockerImage;
    }

    Set<RegistrableConfigurationEntry> getRegisterEndpointUnderEntries() {
        return registerEndpointUnderEntries;
    }
}
