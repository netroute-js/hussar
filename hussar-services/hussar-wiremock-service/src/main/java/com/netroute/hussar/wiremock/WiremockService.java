package com.netroute.hussar.wiremock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import pl.netroute.hussar.core.Endpoint;
import pl.netroute.hussar.core.api.Service;
import pl.netroute.hussar.core.helper.PropertiesHelper;
import pl.netroute.hussar.core.helper.SchemesHelper;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class WiremockService implements Service {
    private static final int HTTP_PORT = 8080;

    private static final Logger LOG = LoggerFactory.getLogger(WiremockService.class);

    private final WiremockServiceConfig config;
    private GenericContainer<?> container;

    WiremockService(WiremockServiceConfig config) {
        Objects.requireNonNull(config, "config is required");

        this.config = config;
    }

    @Override
    public List<Endpoint> getEndpoints() {
        var host = container.getHost();
        var port = container.getMappedPort(HTTP_PORT);
        var endpoint = Endpoint.of(SchemesHelper.HTTP_SCHEME, host, port);

        return List.of(endpoint);
    }

    @Override
    public void start() {
        this.container = configureContainer(config);

        boostrapContainer(config);
        afterContainerBoostrap(config);
    }

    @Override
    public void shutdown() {
        Optional
                .ofNullable(container)
                .ifPresent(this::stopContainer);

        afterContainerShutdown(config);
    }

    @Override
    public String getName() {
        return config.getName();
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
        registerUrlEndpoint(config.getRegisterEndpointUnderProperties());
    }

    private void afterContainerShutdown(WiremockServiceConfig config) {
        deregisterUrlEndpoint(config.getRegisterEndpointUnderProperties());
    }

    private void registerUrlEndpoint(List<String> endpointProperties) {
        if(!endpointProperties.isEmpty()) {
            var endpointAddress = getEndpoints().get(0).getAddress();
            LOG.info("Registering Wiremock endpoint[{}] for properties {}", endpointAddress, endpointProperties);

            endpointProperties.forEach(property -> PropertiesHelper.setProperty(property, endpointAddress));
        }
    }

    private void deregisterUrlEndpoint(List<String> endpointProperties) {
        if(!endpointProperties.isEmpty()) {
            LOG.info("De-registering Wiremock endpoint's related {} properties", endpointProperties);

            endpointProperties.forEach(PropertiesHelper::clearProperty);
        }
    }

    private GenericContainer<?> configureContainer(WiremockServiceConfig config) {
        var dockerImage = config.getDockerImage();

        return new GenericContainer<>(dockerImage)
                .withExposedPorts(HTTP_PORT)
                .withLogConsumer(new Slf4jLogConsumer(LOG));
    }

}
