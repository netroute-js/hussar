package com.netroute.hussar.wiremock;

import pl.netroute.hussar.core.api.MapConfigurationRegistry;
import pl.netroute.hussar.core.helper.ValidatorHelper;
import pl.netroute.hussar.core.service.api.RegistrableConfigurationEntry;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class WiremockServiceConfigurer {
    private static final String NAME_TEMPLATE = "wiremock_service_%s";
    private static final String DOCKER_IMAGE_TEMPLATE = "%s:%s";

    private String name;

    private String dockerImage = "wiremock/wiremock";
    private String dockerImageVersion = "latest";

    private Set<RegistrableConfigurationEntry> registerEndpointUnderEntries = new HashSet<>();

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

    public WiremockServiceConfigurer registerEndpointUnderEntry(RegistrableConfigurationEntry registerEndpointUnderEntry) {
        Objects.requireNonNull(registerEndpointUnderEntry, "registerEndpointUnderEntry is required");

        this.registerEndpointUnderEntries.add(registerEndpointUnderEntry);

        return this;
    }

    public WiremockService configure() {
        var resolvedName = resolveName();
        var resolvedDockerImage = resolveDockerImage();
        var config = new WiremockServiceConfig(resolvedName, resolvedDockerImage, registerEndpointUnderEntries);
        var configRegistry = new MapConfigurationRegistry();

        return new WiremockService(config, configRegistry);
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
