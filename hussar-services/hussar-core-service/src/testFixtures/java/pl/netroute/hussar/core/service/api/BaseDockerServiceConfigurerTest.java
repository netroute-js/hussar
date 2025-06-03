package pl.netroute.hussar.core.service.api;

import lombok.Builder;
import lombok.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.netroute.hussar.core.docker.api.DockerNetwork;
import pl.netroute.hussar.core.network.api.NetworkConfigurer;
import pl.netroute.hussar.core.service.ServiceConfigureContext;
import pl.netroute.hussar.core.stub.helper.StubHelper;

import java.util.function.BiConsumer;

public abstract class BaseDockerServiceConfigurerTest<S extends BaseDockerService<? extends BaseDockerServiceConfig>, C extends BaseDockerServiceConfigurer<S>> {
    protected ServiceConfigurerVerifier verifier;

    @BeforeEach
    public void setup() {
        verifier = new ServiceConfigurerVerifier();
    }

    @Test
    public void shouldConfigureServiceWithGlobalDockerRegistry() {
        // given
        var testMetadata = provideServiceConfigurerWithoutDockerRegistry();
        var configureContext = createServiceConfigureContext();
        var configurer = testMetadata.configurer();
        var assertion = testMetadata.assertion();

        // when
        var service = configurer.configure(configureContext);

        // then
        assertion.accept(service, configureContext);
    }

    @Test
    public void shouldConfigureServiceWithOverriddenDockerRegistry() {
        // given
        var testMetadata = provideServiceConfigurerWithOverriddenDockerRegistry();
        var configureContext = createServiceConfigureContext();
        var configurer = testMetadata.configurer();
        var assertion = testMetadata.assertion();

        // when
        var service = configurer.configure(configureContext);

        // then
        assertion.accept(service, configureContext);
    }

    protected abstract ServiceTestMetadata<C, BiConsumer<S, ServiceConfigureContext>> provideServiceConfigurerWithoutDockerRegistry();
    protected abstract ServiceTestMetadata<C, BiConsumer<S, ServiceConfigureContext>> provideServiceConfigurerWithOverriddenDockerRegistry();

    private ServiceConfigureContext createServiceConfigureContext() {
        var dockerNetwork = StubHelper.defaultStub(DockerNetwork.class);
        var networkConfigurer = StubHelper.defaultStub(NetworkConfigurer.class);

        return ServiceConfigureContext.defaultContext(dockerNetwork, networkConfigurer);
    }

    @Builder(builderMethodName = "newInstance", buildMethodName = "done")
    public record ServiceTestMetadata<C, A>(@NonNull C configurer, @NonNull A assertion) {
    }
}
