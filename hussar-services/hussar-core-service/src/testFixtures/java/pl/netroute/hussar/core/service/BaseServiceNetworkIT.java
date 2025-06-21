package pl.netroute.hussar.core.service;

import lombok.Builder;
import lombok.NonNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.netroute.hussar.core.docker.api.DockerNetwork;
import pl.netroute.hussar.core.network.NetworkOperatorStartupContext;
import pl.netroute.hussar.core.network.ProxyNetworkOperator;
import pl.netroute.hussar.core.network.api.NetworkOperator;
import pl.netroute.hussar.core.service.api.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class BaseServiceNetworkIT<S extends Service> {
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

        dockerNetwork
                .network()
                .close();
    }

    @Test
    public void shouldEnableNetwork() {
        // given
        var serviceContext = ServiceConfigureContext.defaultContext(dockerNetwork, networkOperator.getNetworkConfigurer());
        var networkMetadata = provideEnableNetworkTestMetadata(serviceContext);
        var assertion = networkMetadata.assertion();

        service = networkMetadata.service();

        // when
        service.start(ServiceStartupContext.defaultContext());

        var networkControl = service.getNetworkControl();
        networkControl.disable();
        networkControl.enable();

        // then
        assertion.accept(service);
    }

    @Test
    public void shouldDisableNetwork() {
        // given
        var serviceContext = ServiceConfigureContext.defaultContext(dockerNetwork, networkOperator.getNetworkConfigurer());
        var networkMetadata = provideDisableNetworkTestMetadata(serviceContext);
        var assertion = networkMetadata.assertion();

        service = networkMetadata.service();

        // when
        service.start(ServiceStartupContext.defaultContext());

        var networkControl = service.getNetworkControl();
        networkControl.disable();

        // then
        assertion.accept(service);
    }

    @Test
    public void shouldIntroduceNetworkLatency() {
        // given
        var serviceContext = ServiceConfigureContext.defaultContext(dockerNetwork, networkOperator.getNetworkConfigurer());
        var networkMetadata = provideIntroduceNetworkLatencyTestMetadata(serviceContext);
        var assertion = networkMetadata.assertion();
        var latency = Duration.ofSeconds(2L);

        service = networkMetadata.service();

        // when
        service.start(ServiceStartupContext.defaultContext());

        var networkControl = service.getNetworkControl();
        networkControl.delay(latency);

        var measuredDuration = measureDuration(() -> assertion.accept(service));

        // then
        assertThat(measuredDuration).isGreaterThanOrEqualTo(latency);
    }

    @Test
    public void shouldResetNetwork() {
        // given
        var serviceContext = ServiceConfigureContext.defaultContext(dockerNetwork, networkOperator.getNetworkConfigurer());
        var networkMetadata = provideResetNetworkTestMetadata(serviceContext);
        var assertion = networkMetadata.assertion();
        var latency = Duration.ofSeconds(5L);

        service = networkMetadata.service();

        // when
        service.start(ServiceStartupContext.defaultContext());

        var networkControl = service.getNetworkControl();
        networkControl.disable();
        networkControl.delay(latency);
        networkControl.reset();

        var measuredDuration = measureDuration(() -> assertion.accept(service));

        // then
        assertThat(measuredDuration).isLessThan(latency);
    }

    @Test
    public void shouldSimulateTemporaryNetworkDelay() {
        // given
        var serviceContext = ServiceConfigureContext.defaultContext(dockerNetwork, networkOperator.getNetworkConfigurer());
        var networkMetadata = provideResetNetworkTestMetadata(serviceContext);
        var assertion = networkMetadata.assertion();

        var delayPeriod = Duration.ofSeconds(3L);
        var latency = Duration.ofSeconds(5L);

        service = networkMetadata.service();

        // when
        service.start(ServiceStartupContext.defaultContext());

        service
                .getNetworkControl()
                .scenario()
                .delay(latency)
                .wait(delayPeriod)
                .reset()
                .start();

        var measuredDuration = measureDuration(() -> assertion.accept(service));

        // then
        assertThat(measuredDuration).isBetween(delayPeriod, latency);
    }

    Duration measureDuration(Runnable operation) {
        var start = Instant.now();
        operation.run();
        var end = Instant.now();

        return Duration.between(start, end);
    }

    protected abstract ServiceTestMetadata<S, Consumer<S>> provideEnableNetworkTestMetadata(ServiceConfigureContext configureContext);
    protected abstract ServiceTestMetadata<S, Consumer<S>> provideDisableNetworkTestMetadata(ServiceConfigureContext configureContext);
    protected abstract ServiceTestMetadata<S, Consumer<S>> provideIntroduceNetworkLatencyTestMetadata(ServiceConfigureContext configureContext);
    protected abstract ServiceTestMetadata<S, Consumer<S>> provideResetNetworkTestMetadata(ServiceConfigureContext configureContext);

    @Builder(builderMethodName = "newInstance", buildMethodName = "done")
    public record ServiceTestMetadata<S extends Service, A>(@NonNull S service, @NonNull A assertion) {
    }

}
