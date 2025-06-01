package pl.netroute.hussar.service.wiremock.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import pl.netroute.hussar.core.configuration.api.ConfigurationEntry;
import pl.netroute.hussar.core.configuration.api.DefaultConfigurationRegistry;
import pl.netroute.hussar.core.docker.api.DockerNetwork;
import pl.netroute.hussar.core.network.api.NetworkConfigurer;
import pl.netroute.hussar.core.service.ServiceStartupContext;
import pl.netroute.hussar.core.service.registerer.EndpointRegisterer;
import pl.netroute.hussar.core.stub.helper.StubHelper;

import java.util.List;
import java.util.Set;

import static pl.netroute.hussar.core.helper.SchemesHelper.HTTP_SCHEME;
import static pl.netroute.hussar.core.service.assertion.GenericContainerAssertionHelper.assertContainerExposedPortConfigured;
import static pl.netroute.hussar.core.service.assertion.GenericContainerAssertionHelper.assertContainerLoggingConfigured;
import static pl.netroute.hussar.core.service.assertion.GenericContainerAssertionHelper.assertContainerNetworkConfigured;
import static pl.netroute.hussar.core.service.assertion.GenericContainerAssertionHelper.assertContainerNoEnvVariablesConfigured;
import static pl.netroute.hussar.core.service.assertion.GenericContainerAssertionHelper.assertContainerStarted;
import static pl.netroute.hussar.core.service.assertion.GenericContainerAssertionHelper.assertContainerStopped;
import static pl.netroute.hussar.core.service.assertion.GenericContainerAssertionHelper.assertContainerWaitStrategyConfigured;
import static pl.netroute.hussar.core.service.assertion.ServiceAssertionHelper.assertEndpoints;
import static pl.netroute.hussar.core.service.assertion.ServiceAssertionHelper.assertEntriesRegistered;
import static pl.netroute.hussar.core.service.assertion.ServiceAssertionHelper.assertName;
import static pl.netroute.hussar.core.service.assertion.ServiceAssertionHelper.assertNetworkControl;
import static pl.netroute.hussar.core.service.assertion.ServiceAssertionHelper.assertNoEntriesRegistered;
import static pl.netroute.hussar.core.stub.helper.NetworkConfigurerStubHelper.givenNetworkConfigured;

public class WiremockDockerServiceTest {
    private static final int WIREMOCK_LISTENING_PORT = 8080;

    private static final String WIREMOCK_SERVICE_NAME = "wiremock-service";
    private static final String WIREMOCK_SERVICE_IMAGE = "wiremock/wiremock";

    private DockerNetwork dockerNetwork;
    private NetworkConfigurer networkConfigurer;

    @BeforeEach
    public void setup() {
        dockerNetwork = StubHelper.defaultStub(DockerNetwork.class);
        networkConfigurer = StubHelper.defaultStub(NetworkConfigurer.class);
    }

    @Test
    public void shouldStartMinimalService() {
        // given
        var config = WiremockDockerServiceConfig
                .builder()
                .name(WIREMOCK_SERVICE_NAME)
                .dockerImage(WIREMOCK_SERVICE_IMAGE)
                .scheme(HTTP_SCHEME)
                .registerEndpointUnderProperties(Set.of())
                .registerEndpointUnderEnvironmentVariables(Set.of())
                .build();

        var container = StubHelper.defaultStub(GenericContainer.class);
        var service = createWireMockService(config, container);

        var network = givenNetworkConfigured(networkConfigurer, WIREMOCK_SERVICE_NAME, HTTP_SCHEME, WIREMOCK_LISTENING_PORT);

        // when
        service.start(ServiceStartupContext.defaultContext());

        // then
        assertContainerStarted(container);
        assertContainerExposedPortConfigured(container, WIREMOCK_LISTENING_PORT);
        assertContainerWaitStrategyConfigured(container, Wait.forListeningPort());
        assertContainerNetworkConfigured(container, dockerNetwork);
        assertContainerLoggingConfigured(container);
        assertContainerNoEnvVariablesConfigured(container);
        assertName(service, WIREMOCK_SERVICE_NAME);
        assertEndpoints(service, network);
        assertNetworkControl(service);
        assertNoEntriesRegistered(service);
    }

    @Test
    public void shouldStartExtendedService() {
        // given
        var endpointProperty = "endpoint.url";
        var endpointEnvVariable = "ENDPOINT_URL";

        var config = WiremockDockerServiceConfig
                .builder()
                .name(WIREMOCK_SERVICE_NAME)
                .dockerImage(WIREMOCK_SERVICE_IMAGE)
                .scheme(HTTP_SCHEME)
                .registerEndpointUnderProperties(Set.of(endpointProperty))
                .registerEndpointUnderEnvironmentVariables(Set.of(endpointEnvVariable))
                .build();

        var container = StubHelper.defaultStub(GenericContainer.class);
        var service = createWireMockService(config, container);

        var network = givenNetworkConfigured(networkConfigurer, WIREMOCK_SERVICE_NAME, HTTP_SCHEME, WIREMOCK_LISTENING_PORT);

        // when
        service.start(ServiceStartupContext.defaultContext());

        // then
        var endpoint = network.getEndpoints().getFirst();
        var endpointPropertyEntry = ConfigurationEntry.property(endpointProperty, endpoint.address());
        var endpointEnvVariableEntry = ConfigurationEntry.envVariable(endpointEnvVariable, endpoint.address());
        var endpointEntries = List.<ConfigurationEntry>of(endpointPropertyEntry, endpointEnvVariableEntry);

        assertContainerStarted(container);
        assertContainerExposedPortConfigured(container, WIREMOCK_LISTENING_PORT);
        assertContainerWaitStrategyConfigured(container, Wait.forListeningPort());
        assertContainerNetworkConfigured(container, dockerNetwork);
        assertContainerLoggingConfigured(container);
        assertContainerNoEnvVariablesConfigured(container);
        assertName(service, WIREMOCK_SERVICE_NAME);
        assertEndpoints(service, network);
        assertNetworkControl(service);
        assertEntriesRegistered(service, endpointEntries);
    }

    @Test
    public void shouldShutdownService() {
        // given
        var config = WiremockDockerServiceConfig
                .builder()
                .name(WIREMOCK_SERVICE_NAME)
                .dockerImage(WIREMOCK_SERVICE_IMAGE)
                .scheme(HTTP_SCHEME)
                .registerEndpointUnderProperties(Set.of())
                .registerEndpointUnderEnvironmentVariables(Set.of())
                .build();

        var container = StubHelper.defaultStub(GenericContainer.class);
        var service = createWireMockService(config, container);

        // when
        service.shutdown();

        // then
        assertContainerStopped(container);
    }

    private WiremockDockerService createWireMockService(WiremockDockerServiceConfig config,
                                                        GenericContainer<?> container) {
        var configurationRegistry = new DefaultConfigurationRegistry();
        var endpointRegisterer = new EndpointRegisterer(configurationRegistry);

        return new WiremockDockerService(container, dockerNetwork, config, configurationRegistry, endpointRegisterer, networkConfigurer);
    }

}
