package com.netroute.hussar.wiremock;

import pl.netroute.hussar.core.helper.ValidatorHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WiremockServiceConfigurer {
    private static final String NAME_TEMPLATE = "wiremock_service_%s";
    private static final String DOCKER_IMAGE_TEMPLATE = "%s:%s";

    private String name;

    private String dockerImage = "wiremock/wiremock";
    private String dockerImageVersion = "latest";

    private List<String> registerEndpointUnderProperties = new ArrayList<>();

    public WiremockServiceConfigurer name(String name) {
        ValidatorHelper.requireNonEmpty(name, "name");

        this.name = name;

        return this;
    }

    public WiremockServiceConfigurer dockerImageVersion(String dockerImageVersion) {
        ValidatorHelper.requireNonEmpty(dockerImageVersion, "dockerImageVersion");

        this.dockerImageVersion = dockerImageVersion;

        return this;
    }

    public WiremockServiceConfigurer registerEndpointUnderProperty(String property) {
        ValidatorHelper.requireNonEmpty(property, "property");

        this.registerEndpointUnderProperties.add(property);

        return this;
    }

    public WiremockService configure() {
        var resolvedName = resolveName();
        var resolvedDockerImage = resolveDockerImage();
        var config = new WiremockServiceConfig(resolvedName, resolvedDockerImage, registerEndpointUnderProperties);

        return new WiremockService(config);
    }

    private String resolveName() {
        return ValidatorHelper.requireNonEmptyOrElseGet(this.name, this::resolveDefaultName);
    }

    private String resolveDefaultName() {
        return String.format(NAME_TEMPLATE, UUID.randomUUID().toString());
    }

    private String resolveDockerImage() {
        return String.format(DOCKER_IMAGE_TEMPLATE, dockerImage, dockerImageVersion);
    }

    public static WiremockServiceConfigurer newInstance() {
        return new WiremockServiceConfigurer();
    }

}
