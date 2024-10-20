package pl.netroute.hussar.core.service;

import lombok.NonNull;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import pl.netroute.hussar.core.api.configuration.ConfigurationRegistry;
import pl.netroute.hussar.core.api.Endpoint;
import pl.netroute.hussar.core.api.service.Service;
import pl.netroute.hussar.core.api.service.ServiceStartupContext;
import pl.netroute.hussar.core.helper.SchemesHelper;
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
     * Creates new instance of {@link BaseDockerService}.
     *
     * @param container - the {@link GenericContainer} used by this {@link BaseDockerService}.
     * @param config - the {@link BaseDockerServiceConfig} used by this {@link BaseDockerService}.
     * @param configurationRegistry - the {@link ConfigurationRegistry} used by this {@link BaseDockerService}.
     * @param endpointRegisterer - the {@link EndpointRegisterer} used by this {@link BaseDockerService}.
     */
    protected BaseDockerService(@NonNull GenericContainer<?> container,
                                @NonNull C config,
                                @NonNull ConfigurationRegistry configurationRegistry,
                                @NonNull EndpointRegisterer endpointRegisterer) {
        super(config, configurationRegistry, endpointRegisterer);

        this.container = container;
    }

    @Override
    public List<Endpoint> getEndpoints() {
        var host = container.getHost();
        var scheme = Optional
                .ofNullable(config.getScheme())
                .orElse(SchemesHelper.EMPTY_SCHEME);

        return container
                .getExposedPorts()
                .stream()
                .map(container::getMappedPort)
                .map(mappedPort -> Endpoint.of(scheme, host, mappedPort))
                .toList();
    }

    @Override
    protected final void bootstrapService(ServiceStartupContext context) {
        log.info("Using DockerImage[{}] for {} Service", config.getDockerImage(), config.getName());

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
        configureLogging(container);
        configureWaitStrategy(container);
    }

    /**
     * Configure logging of the {@link GenericContainer}.
     *
     * @param container - the {@link GenericContainer} to configure.
     */
    protected void configureLogging(GenericContainer<?> container) {
        container.withLogConsumer(new Slf4jLogConsumer(log));
    }

    /**
     * Configure wait strategy of {@link GenericContainer}.
     *
     * @param container - the {@link GenericContainer} to configure.
     */
    protected void configureWaitStrategy(GenericContainer<?> container) {
        container.waitingFor(Wait.forListeningPort());
    }

}
