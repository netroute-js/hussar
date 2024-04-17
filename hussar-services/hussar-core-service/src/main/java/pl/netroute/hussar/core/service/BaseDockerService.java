package pl.netroute.hussar.core.service;

import lombok.NonNull;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import pl.netroute.hussar.core.Endpoint;
import pl.netroute.hussar.core.api.ConfigurationRegistry;
import pl.netroute.hussar.core.api.ServiceStartupContext;
import pl.netroute.hussar.core.helper.SchemesHelper;
import pl.netroute.hussar.core.service.registerer.EndpointRegisterer;

import java.util.List;
import java.util.Optional;

public abstract class BaseDockerService<C extends BaseDockerServiceConfig> extends BaseService<C> {
    protected final GenericContainer<?> container;

    public BaseDockerService(@NonNull GenericContainer<?> container,
                             @NonNull C config,
                             @NonNull ConfigurationRegistry configurationRegistry,
                             @NonNull EndpointRegisterer endpointRegisterer) {
        super(config, configurationRegistry, endpointRegisterer);

        this.container = container;
    }

    @Override
    public final List<Endpoint> getEndpoints() {
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

    protected void configureContainer(GenericContainer<?> container) {
        configureLogging(container);
        configureWaitStrategy(container);
    }

    protected void configureLogging(GenericContainer<?> container) {
        container.withLogConsumer(new Slf4jLogConsumer(log));
    }

    protected void configureWaitStrategy(GenericContainer<?> container) {
        container.waitingFor(Wait.forListeningPort());
    }

}
