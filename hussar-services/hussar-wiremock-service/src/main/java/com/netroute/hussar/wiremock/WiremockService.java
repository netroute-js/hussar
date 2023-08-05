package com.netroute.hussar.wiremock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import pl.netroute.hussar.core.Endpoint;
import pl.netroute.hussar.core.api.ConfigurationRegistry;
import pl.netroute.hussar.core.api.Service;
import pl.netroute.hussar.core.api.ServiceStartupContext;
import pl.netroute.hussar.core.helper.SchemesHelper;
import pl.netroute.hussar.core.service.api.RegistrableConfigurationEntry;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class WiremockService implements Service {
    private static final int HTTP_PORT = 8080;

    private static final Logger LOG = LoggerFactory.getLogger(WiremockService.class);

    private final WiremockServiceConfig config;
    private final ConfigurationRegistry configRegistry;
    private GenericContainer<?> container;

    WiremockService(WiremockServiceConfig config, ConfigurationRegistry configRegistry) {
        Objects.requireNonNull(config, "config is required");
        Objects.requireNonNull(configRegistry, "configRegistry is required");

        this.config = config;
        this.configRegistry = configRegistry;
    }

    @Override
    public List<Endpoint> getEndpoints() {
        var host = container.getHost();
        var port = container.getMappedPort(HTTP_PORT);
        var endpoint = Endpoint.of(SchemesHelper.HTTP_SCHEME, host, port);

        return List.of(endpoint);
    }

    @Override
    public void start(ServiceStartupContext context) {
        Objects.requireNonNull(context, "context is required");

        this.container = configureContainer(config);

        boostrapContainer(config);
        afterContainerBoostrap(config);
    }

    @Override
    public void shutdown() {
        Optional
                .ofNullable(container)
                .ifPresent(this::stopContainer);
    }

    @Override
    public String getName() {
        return config.getName();
    }

    @Override
    public ConfigurationRegistry getConfigurationRegistry() {
        return configRegistry;
    }

    private void boostrapContainer(WiremockServiceConfig config) {
        var name = config.getName();
        var dockerImage = config.getDockerImage();
        LOG.info("Starting {}[{}] container", name, dockerImage);

        container.start();
    }

    private void stopContainer(GenericContainer<?> container) {
        var name = config.getName();
        var dockerImage = config.getDockerImage();
        LOG.info("Stopping {}[{}] container", name, dockerImage);

        container.stop();
    }

    private void afterContainerBoostrap(WiremockServiceConfig config) {
        registerUrlEndpoint(config.getRegisterEndpointUnderEntries());
    }

    private void registerUrlEndpoint(Set<RegistrableConfigurationEntry> endpointEntries) {
        if(!endpointEntries.isEmpty()) {
            var endpointAddress = getEndpoints().get(0).getAddress();

            endpointEntries
                    .stream()
                    .map(registrableEntry -> registrableEntry.toResolvedConfigurationEntry(endpointAddress))
                    .forEach(configRegistry::register);
        }
    }

    private GenericContainer<?> configureContainer(WiremockServiceConfig config) {
        var dockerImage = config.getDockerImage();

        return new GenericContainer<>(dockerImage)
                .withExposedPorts(HTTP_PORT)
                .withLogConsumer(new Slf4jLogConsumer(LOG));
    }

}
