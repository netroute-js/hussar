package pl.netroute.hussar.core.service;

import lombok.Builder;
import lombok.NonNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.netroute.hussar.core.api.Endpoint;
import pl.netroute.hussar.core.docker.api.DockerNetwork;
import pl.netroute.hussar.core.network.NetworkOperatorStartupContext;
import pl.netroute.hussar.core.network.ProxyNetworkOperator;
import pl.netroute.hussar.core.network.api.NetworkOperator;
import pl.netroute.hussar.core.service.api.Service;

import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public abstract class BaseServiceIT<S extends Service> {
    protected DockerNetwork dockerNetwork;
    protected NetworkOperator networkOperator;
    protected S service;

    @BeforeEach
    public void setup() {
        dockerNetwork = DockerNetwork.newNetwork();

        networkOperator = ProxyNetworkOperator.newInstance(dockerNetwork);
        networkOperator.start(NetworkOperatorStartupContext.defaultContext());
    }

    @AfterEach
    public void cleanup() {
        Optional.ofNullable(networkOperator)
                .ifPresent(NetworkOperator::shutdown);

        Optional.ofNullable(service)
                .ifPresent(Service::shutdown);
    }

    @Test
    void shouldStartMinimallyConfiguredService() {
        // given
        var serviceContext = ServiceConfigureContext.defaultContext(dockerNetwork, networkOperator.getNetworkConfigurer());
        var serviceMetadata = provideMinimallyConfiguredServiceTestMetadata(serviceContext);
        var assertion = serviceMetadata.assertion();

        service = serviceMetadata.service();

        // when
        service.start(ServiceStartupContext.defaultContext());

        // then
        assertion.accept(service);
    }

    @Test
    void shouldStartFullyConfiguredService() {
        // given
        var serviceContext = ServiceConfigureContext.defaultContext(dockerNetwork, networkOperator.getNetworkConfigurer());
        var serviceMetadata = provideFullyConfiguredServiceTestMetadata(serviceContext);
        var assertion = serviceMetadata.assertion();

        service = serviceMetadata.service();

        // when
        service.start(ServiceStartupContext.defaultContext());

        // then
        assertion.accept(service);
    }

    @Test
    void shouldShutdownService() {
        // given
        var serviceContext = ServiceConfigureContext.defaultContext(dockerNetwork, networkOperator.getNetworkConfigurer());
        var serviceMetadata = provideShutdownServiceTestMetadata(serviceContext);
        var assertion = serviceMetadata.assertion();

        service = serviceMetadata.service();

        // when
        service.start(ServiceStartupContext.defaultContext());

        var endpoints = service.getEndpoints();

        service.shutdown();

        // then
        assertion.accept(service, endpoints);
    }

    protected abstract ServiceTestMetadata<S, Consumer<S>> provideMinimallyConfiguredServiceTestMetadata(ServiceConfigureContext context);
    protected abstract ServiceTestMetadata<S, Consumer<S>> provideFullyConfiguredServiceTestMetadata(ServiceConfigureContext context);
    protected abstract ServiceTestMetadata<S, BiConsumer<S, List<Endpoint>>> provideShutdownServiceTestMetadata(ServiceConfigureContext context);

    @Builder(builderMethodName = "newInstance", buildMethodName = "done")
    public record ServiceTestMetadata<S extends Service, A>(@NonNull S service, @NonNull A assertion) {
    }

}
