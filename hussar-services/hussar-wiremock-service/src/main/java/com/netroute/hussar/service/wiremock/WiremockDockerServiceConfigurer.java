package com.netroute.hussar.service.wiremock;

import pl.netroute.hussar.core.helper.SchemesHelper;
import pl.netroute.hussar.core.service.BaseDockerServiceConfigurer;
import pl.netroute.hussar.core.service.api.RegistrableConfigurationEntry;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class WiremockDockerServiceConfigurer extends BaseDockerServiceConfigurer<WiremockDockerService, WiremockDockerServiceConfigurer> {
    private static final String DOCKER_IMAGE = "wiremock/wiremock";
    private static final String SERVICE = "wiremock_service";

    private Set<RegistrableConfigurationEntry> registerEndpointUnderEntries = new HashSet<>();

    private WiremockDockerServiceConfigurer() {
        super(DOCKER_IMAGE, SchemesHelper.HTTP_SCHEME);
    }

    public WiremockDockerServiceConfigurer registerEndpointUnderEntry(RegistrableConfigurationEntry registerEndpointUnderEntry) {
        Objects.requireNonNull(registerEndpointUnderEntry, "registerEndpointUnderEntry is required");

        this.registerEndpointUnderEntries.add(registerEndpointUnderEntry);

        return this;
    }

    public WiremockDockerService configure() {
        var config = createConfig();

        return new WiremockDockerService(config);
    }

    private WiremockDockerServiceConfig createConfig() {
        var resolvedName = resolveName(SERVICE);
        var resolvedDockerImage = resolveDockerImage();
        var scheme = getScheme();

        return new WiremockDockerServiceConfig(resolvedName, resolvedDockerImage, scheme, registerEndpointUnderEntries);
    }

    public static WiremockDockerServiceConfigurer newInstance() {
        return new WiremockDockerServiceConfigurer();
    }

}
