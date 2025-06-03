package pl.netroute.hussar.core.docker;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.netroute.hussar.core.docker.api.DockerNetwork;
import pl.netroute.hussar.core.docker.api.DockerRegistry;
import pl.netroute.hussar.core.network.api.NetworkConfigurer;
import pl.netroute.hussar.core.service.ServiceConfigureContext;
import pl.netroute.hussar.core.stub.helper.StubHelper;

public class DockerRegistryResolverTest {
    private static final DockerRegistry NO_REGISTRY = null;

    private DockerRegistryResolverVerifier verifier;

    @BeforeEach
    public void setup() {
        verifier = new DockerRegistryResolverVerifier();
    }

    @Test
    public void shouldResolveGlobalRegistry() {
        // given
        var serviceConfigureContext = createServiceConfigureContext();

        // when
        var dockerRegistry = DockerRegistryResolver.resolve(NO_REGISTRY, serviceConfigureContext);

        // then
        verifier.verifyDockerRegistryResolved(dockerRegistry, serviceConfigureContext.dockerRegistry());
    }

    @Test
    public void shouldResolveOverriddenRegistry() {
        // given
        var serviceConfigureContext = createServiceConfigureContext();
        var overriddenRegistry = new DockerRegistry("docker-registry.netroute.pl");

        // when
        var dockerRegistry = DockerRegistryResolver.resolve(overriddenRegistry, serviceConfigureContext);

        // then
        verifier.verifyDockerRegistryResolved(dockerRegistry, overriddenRegistry);
    }

    private ServiceConfigureContext createServiceConfigureContext() {
        var dockerRegistry = DockerRegistry.defaultRegistry();
        var dockerNetwork = DockerNetwork.newNetwork();
        var networkConfigurer = StubHelper.defaultStub(NetworkConfigurer.class);

        return new ServiceConfigureContext(dockerRegistry, dockerNetwork, networkConfigurer);
    }

}