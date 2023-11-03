package pl.netroute.hussar.core.service;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import pl.netroute.hussar.core.Endpoint;
import pl.netroute.hussar.core.api.ServiceStartupContext;
import pl.netroute.hussar.core.helper.SchemesHelper;

import java.util.List;
import java.util.stream.Collectors;

public abstract class BaseDockerService<C extends BaseDockerServiceConfig> extends BaseService<C> {
    protected final GenericContainer<?> container;

    public BaseDockerService(C config) {
        super(config);

        this.container = new GenericContainer<>(config.getDockerImage());
    }

    @Override
    public final List<Endpoint> getEndpoints() {
        var host = container.getHost();
        var scheme = config.getScheme().orElse(SchemesHelper.EMPTY_SCHEME);

        return container
                .getExposedPorts()
                .stream()
                .map(container::getMappedPort)
                .map(mappedPort -> Endpoint.of(scheme, host, mappedPort))
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    protected final void bootstrapService(ServiceStartupContext context) {
        log.info("Bootstrapping Docker[{},{}] Service", config.getName(), config.getDockerImage());

        configureContainer(container);
        container.start();
    }

    @Override
    protected final void shutdownService() {
        log.info("Shutting down Docker[{},{}] Service", config.getName(), config.getDockerImage());

        container.stop();
    }

    protected void configureContainer(GenericContainer<?> container) {
        configureLogging(container);
    }

    private void configureLogging(GenericContainer<?> container) {
        container.withLogConsumer(new Slf4jLogConsumer(log));
    }

}
