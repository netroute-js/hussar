package com.netroute.hussar.service.wiremock;

import lombok.NonNull;
import org.testcontainers.containers.GenericContainer;
import pl.netroute.hussar.core.service.BaseDockerService;

public class WiremockDockerService extends BaseDockerService<WiremockDockerServiceConfig> {
    private static final int HTTP_LISTENING_PORT = 8080;

    WiremockDockerService(@NonNull WiremockDockerServiceConfig config) {
        super(config);
    }

    @Override
    protected void configureContainer(GenericContainer<?> container) {
        super.configureContainer(container);

        container.withExposedPorts(HTTP_LISTENING_PORT);
    }

}
