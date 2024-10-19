package pl.netroute.hussar.service.wiremock;

import lombok.NonNull;
import org.testcontainers.containers.GenericContainer;
import pl.netroute.hussar.core.api.configuration.ConfigurationRegistry;
import pl.netroute.hussar.core.api.service.Service;
import pl.netroute.hussar.core.service.BaseDockerService;
import pl.netroute.hussar.core.service.registerer.EndpointRegisterer;

/**
 * Hussar Docker {@link Service} representing WireMock.
 */
public class WiremockDockerService extends BaseDockerService<WiremockDockerServiceConfig> {
    private static final int HTTP_LISTENING_PORT = 8080;

    /**
     * Creates new {@link WiremockDockerService}.
     *
     * @param container - the {@link GenericContainer} used by this {@link WiremockDockerService}.
     * @param config - the {@link WiremockDockerServiceConfig} used by this {@link WiremockDockerService}.
     * @param configurationRegistry - the {@link ConfigurationRegistry} used by this {@link WiremockDockerService}.
     * @param endpointRegisterer - the  {@link EndpointRegisterer} used by this {@link WiremockDockerService}.
     */
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
