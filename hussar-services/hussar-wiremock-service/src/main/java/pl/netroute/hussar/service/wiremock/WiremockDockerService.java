package pl.netroute.hussar.service.wiremock;

import lombok.NonNull;
import org.testcontainers.containers.GenericContainer;
import pl.netroute.hussar.core.api.ConfigurationRegistry;
import pl.netroute.hussar.core.service.BaseDockerService;
import pl.netroute.hussar.core.service.registerer.EndpointRegisterer;

public class WiremockDockerService extends BaseDockerService<WiremockDockerServiceConfig> {
    private static final int HTTP_LISTENING_PORT = 8080;

    WiremockDockerService(@NonNull GenericContainer<?> container,
                          @NonNull WiremockDockerServiceConfig config,
                          @NonNull ConfigurationRegistry configurationRegistry,
                          @NonNull EndpointRegisterer endpointRegisterer) {
        super(container, config, configurationRegistry, endpointRegisterer);
    }

    @Override
    protected void configureContainer(GenericContainer<?> container) {
        super.configureContainer(container);

        container.withExposedPorts(HTTP_LISTENING_PORT);
    }

}
