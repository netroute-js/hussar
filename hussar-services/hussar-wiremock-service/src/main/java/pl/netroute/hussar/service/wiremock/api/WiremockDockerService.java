package pl.netroute.hussar.service.wiremock.api;

import lombok.NonNull;
import org.testcontainers.containers.GenericContainer;
import pl.netroute.hussar.core.configuration.api.ConfigurationRegistry;
import pl.netroute.hussar.core.docker.api.DockerNetwork;
import pl.netroute.hussar.core.network.api.NetworkConfigurer;
import pl.netroute.hussar.core.service.api.BaseDockerService;
import pl.netroute.hussar.core.service.api.Service;
import pl.netroute.hussar.core.service.registerer.EndpointRegisterer;

import java.util.List;

/**
 * Hussar Docker {@link Service} representing WireMock.
 */
public class WiremockDockerService extends BaseDockerService<WiremockDockerServiceConfig> {
    private static final int HTTP_LISTENING_PORT = 8080;

    /**
     * Creates new {@link WiremockDockerService}.
     *
     * @param container - the {@link GenericContainer} used by this {@link WiremockDockerService}.
     * @param dockerNetwork - the {@link DockerNetwork} used by this {@link WiremockDockerService}.
     * @param config - the {@link WiremockDockerServiceConfig} used by this {@link WiremockDockerService}.
     * @param configurationRegistry - the {@link ConfigurationRegistry} used by this {@link WiremockDockerService}.
     * @param endpointRegisterer - the  {@link EndpointRegisterer} used by this {@link WiremockDockerService}.
     * @param networkConfigurer - the  {@link NetworkConfigurer} used by this {@link WiremockDockerService}.
     */
    WiremockDockerService(@NonNull GenericContainer<?> container,
                          @NonNull DockerNetwork dockerNetwork,
                          @NonNull WiremockDockerServiceConfig config,
                          @NonNull ConfigurationRegistry configurationRegistry,
                          @NonNull EndpointRegisterer endpointRegisterer,
                          @NonNull NetworkConfigurer networkConfigurer) {
        super(container, dockerNetwork, config, configurationRegistry, endpointRegisterer, networkConfigurer);
    }

    @Override
    protected List<Integer> getInternalPorts() {
        return List.of(HTTP_LISTENING_PORT);
    }

}
