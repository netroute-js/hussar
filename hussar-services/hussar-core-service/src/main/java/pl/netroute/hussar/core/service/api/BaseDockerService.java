package pl.netroute.hussar.core.service.api;

import lombok.NonNull;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import pl.netroute.hussar.core.api.Endpoint;
import pl.netroute.hussar.core.configuration.api.ConfigurationRegistry;
import pl.netroute.hussar.core.docker.DockerAliasGenerator;
import pl.netroute.hussar.core.docker.api.DockerNetwork;
import pl.netroute.hussar.core.helper.SchemesHelper;
import pl.netroute.hussar.core.network.api.NetworkConfigurer;
import pl.netroute.hussar.core.service.ServiceStartupContext;
import pl.netroute.hussar.core.service.registerer.EndpointRegisterer;

import java.util.List;
import java.util.Optional;

/**
 * A base class with default implementation/template for all Hussar Docker {@link Service}.
 *
 * @param <C> the {@link BaseDockerServiceConfig} parameter used by the {@link BaseDockerService}.
 */
public abstract class BaseDockerService<C extends BaseDockerServiceConfig> extends BaseService<C> {

    /**
     * An instance of {@link GenericContainer}. This is actual Docker container.
     */
    protected final GenericContainer<?> container;

    /**
     * An instance of {@link DockerNetwork}. This is actual Docker network.
     */
    protected final DockerNetwork dockerNetwork;

    /**
     * Docker alias used in the Network.
     */
    protected final String dockerAlias;

    /**
     * Creates new instance of {@link BaseDockerService}.
     *
     * @param container - the {@link GenericContainer} used by this {@link BaseDockerService}.
     * @param dockerNetwork - the {@link DockerNetwork} used by this {@link BaseDockerService}.
     * @param config - the {@link BaseDockerServiceConfig} used by this {@link BaseDockerService}.
     * @param configurationRegistry - the {@link ConfigurationRegistry} used by this {@link BaseDockerService}.
     * @param endpointRegisterer - the {@link EndpointRegisterer} used by this {@link BaseDockerService}.
     * @param networkConfigurer - the {@link NetworkConfigurer} used by this {@link BaseDockerService}.
     */
    protected BaseDockerService(@NonNull GenericContainer<?> container,
                                @NonNull DockerNetwork dockerNetwork,
                                @NonNull C config,
                                @NonNull ConfigurationRegistry configurationRegistry,
                                @NonNull EndpointRegisterer endpointRegisterer,
                                @NonNull NetworkConfigurer networkConfigurer) {
        super(config, configurationRegistry, endpointRegisterer, networkConfigurer);

        this.container = container;
        this.dockerNetwork = dockerNetwork;
        this.dockerAlias = DockerAliasGenerator.generate();
    }

    @Override
    protected List<Endpoint> getInternalEndpoints() {
        var scheme = Optional
                .ofNullable(config.getScheme())
                .orElse(SchemesHelper.EMPTY_SCHEME);

        var internalPorts = getInternalPorts();

        return internalPorts
                .stream()
                .map(internalPort -> Endpoint.of(scheme, dockerAlias, internalPort))
                .toList();
    }

    @Override
    protected final void bootstrapService(ServiceStartupContext context) {
        configureContainer(container);
        container.start();
    }

    @Override
    protected final void shutdownService() {
        container.stop();
    }

    /**
     * Configures container with default configurations.
     *
     * @param container - the {@link GenericContainer} to configure.
     */
    protected void configureContainer(GenericContainer<?> container) {
        configureDockerNetwork(container);
        configureExposedPorts(container);
        configureEnvVariables(container);
        configureLogging(container);
        configureWaitStrategy(container);
        configureStartupTimeout(container);
    }

    /**
     * Configures Docker network of the {@link GenericContainer}.
     *
     * @param container - the {@link GenericContainer} to configure.
     */
    protected void configureDockerNetwork(GenericContainer<?> container) {
        var network = dockerNetwork.network();

        container.withNetwork(network);
        container.withNetworkAliases(dockerAlias);
    }

    /**
     * Configures exposed ports of {@link GenericContainer}.
     *
     * @param container - the {@link GenericContainer} to configure.
     */
    protected void configureExposedPorts(GenericContainer<?> container) {
        getInternalPorts().forEach(container::withExposedPorts);
    }

    /**
     * Configures logging of the {@link GenericContainer}.
     *
     * @param container - the {@link GenericContainer} to configure.
     */
    protected void configureLogging(GenericContainer<?> container) {
        container.withLogConsumer(new Slf4jLogConsumer(log));
    }

    /**
     * Configures wait strategy of {@link GenericContainer}.
     *
     * @param container - the {@link GenericContainer} to configure.
     */
    protected void configureWaitStrategy(GenericContainer<?> container) {
        container.waitingFor(Wait.forListeningPort());
    }

    protected void configureStartupTimeout(GenericContainer<?> container) {
        var startupTimeout = config.getStartupTimeout();

        container.withStartupTimeout(startupTimeout);
    }

    /**
     * Configures environment variables of {@link GenericContainer}.
     *
     * @param container - the {@link GenericContainer} to configure.
     */
    protected void configureEnvVariables(GenericContainer<?> container) {
    }

    /**
     * Returns internal ports used by this {@link BaseDockerService}.
     *
     * @return internal ports.
     */
    protected abstract List<Integer> getInternalPorts();
}
