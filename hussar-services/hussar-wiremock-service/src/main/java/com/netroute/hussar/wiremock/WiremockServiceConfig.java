package com.netroute.hussar.wiremock;

import pl.netroute.hussar.core.helper.ValidatorHelper;

import java.util.List;
import java.util.Objects;

class WiremockServiceConfig {
    private final String name;
    private final String dockerImage;
    private final List<String> registerEndpointUnderProperties;

    WiremockServiceConfig(String name, String dockerImage, List<String> registerEndpointUnderProperties) {
        ValidatorHelper.requireNonEmpty(name, "name is required");
        ValidatorHelper.requireNonEmpty(dockerImage, "dockerImage is required");
        Objects.requireNonNull(registerEndpointUnderProperties, "registerEndpointUnderProperties is required");

        this.name = name;
        this.dockerImage = dockerImage;
        this.registerEndpointUnderProperties = registerEndpointUnderProperties;
    }

    String getName() {
        return name;
    }

    String getDockerImage() {
        return dockerImage;
    }

    List<String> getRegisterEndpointUnderProperties() {
        return registerEndpointUnderProperties;
    }
}
