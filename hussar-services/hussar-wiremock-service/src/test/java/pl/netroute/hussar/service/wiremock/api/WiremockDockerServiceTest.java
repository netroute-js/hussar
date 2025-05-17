package pl.netroute.hussar.service.wiremock.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import pl.netroute.hussar.core.api.Endpoint;
import pl.netroute.hussar.core.configuration.api.ConfigurationEntry;
import pl.netroute.hussar.core.configuration.api.DefaultConfigurationRegistry;
import pl.netroute.hussar.core.helper.SchemesHelper;
import pl.netroute.hussar.core.network.api.NetworkConfigurer;
import pl.netroute.hussar.core.service.ServiceStartupContext;
import pl.netroute.hussar.core.service.registerer.EndpointRegisterer;
import pl.netroute.hussar.core.stub.helper.GenericContainerStubHelper.GenericContainerAccessibility;
import pl.netroute.hussar.core.stub.helper.StubHelper;

import java.util.List;
import java.util.Set;

import static pl.netroute.hussar.core.assertion.helper.NetworkConfigurerAssertionHelper.assertNetworkConfigured;
import static pl.netroute.hussar.core.service.assertion.GenericContainerAssertionHelper.assertContainerExposedPortConfigured;
import static pl.netroute.hussar.core.service.assertion.GenericContainerAssertionHelper.assertContainerLoggingConfigured;
import static pl.netroute.hussar.core.service.assertion.GenericContainerAssertionHelper.assertContainerNoEnvVariablesConfigured;
import static pl.netroute.hussar.core.service.assertion.GenericContainerAssertionHelper.assertContainerStarted;
import static pl.netroute.hussar.core.service.assertion.GenericContainerAssertionHelper.assertContainerStopped;
import static pl.netroute.hussar.core.service.assertion.GenericContainerAssertionHelper.assertContainerWaitStrategyConfigured;
import static pl.netroute.hussar.core.service.assertion.ServiceAssertionHelper.assertEntriesRegistered;
import static pl.netroute.hussar.core.service.assertion.ServiceAssertionHelper.assertName;
import static pl.netroute.hussar.core.service.assertion.ServiceAssertionHelper.assertNetworkControl;
import static pl.netroute.hussar.core.service.assertion.ServiceAssertionHelper.assertNoEntriesRegistered;
import static pl.netroute.hussar.core.service.assertion.ServiceAssertionHelper.assertSingleEndpoint;
import static pl.netroute.hussar.core.stub.helper.GenericContainerStubHelper.givenContainerAccessible;
import static pl.netroute.hussar.core.stub.helper.NetworkConfigurerStubHelper.givenNetworkConfigured;

public class WiremockDockerServiceTest {
    private static final String WIREMOCK_HOST = "localhost";
    private static final int WIREMOCK_LISTENING_PORT = 8080;
    private static final int WIREMOCK_MAPPED_PORT = 9080;

    private static final String WIREMOCK_SERVICE_NAME = "wiremock-service";
    private static final String WIREMOCK_SERVICE_IMAGE = "wiremock/wiremock";

    private NetworkConfigurer networkConfigurer;

    private GenericContainerAccessibility containerAccessibility;

    @BeforeEach
    public void setup() {
        networkConfigurer = StubHelper.defaultStub(NetworkConfigurer.class);

        containerAccessibility = GenericContainerAccessibility
                .builder()
                .host(WIREMOCK_HOST)
                .exposedPort(WIREMOCK_LISTENING_PORT)
                .mappedPort(WIREMOCK_LISTENING_PORT, WIREMOCK_MAPPED_PORT)
                .build();
    }

    @Test
    public void shouldStartMinimalService() {
        // given
        var config = WiremockDockerServiceConfig
                .builder()
                .name(WIREMOCK_SERVICE_NAME)
                .dockerImage(WIREMOCK_SERVICE_IMAGE)
                .scheme(SchemesHelper.HTTP_SCHEME)
                .registerEndpointUnderProperties(Set.of())
                .registerEndpointUnderEnvironmentVariables(Set.of())
                .build();

        var endpoint = Endpoint.of(SchemesHelper.HTTP_SCHEME, WIREMOCK_HOST, WIREMOCK_MAPPED_PORT);

        var container = StubHelper.defaultStub(GenericContainer.class);
        var service = createWireMockService(config, container);

        givenContainerAccessible(container, containerAccessibility);
        givenNetworkConfigured(networkConfigurer, WIREMOCK_SERVICE_NAME, endpoint);

        // when
        service.start(ServiceStartupContext.defaultContext());

        // then
        assertContainerStarted(container);
        assertContainerExposedPortConfigured(container, WIREMOCK_LISTENING_PORT);
        assertContainerWaitStrategyConfigured(container, Wait.forListeningPort());
        assertContainerLoggingConfigured(container);
        assertContainerNoEnvVariablesConfigured(container);
        assertName(service, WIREMOCK_SERVICE_NAME);
        assertSingleEndpoint(service, endpoint);
        assertNetworkControl(service);
        assertNoEntriesRegistered(service);
        assertNetworkConfigured(networkConfigurer, WIREMOCK_SERVICE_NAME, endpoint);
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
                .scheme(SchemesHelper.HTTP_SCHEME)
                .registerEndpointUnderProperties(Set.of(endpointProperty))
                .registerEndpointUnderEnvironmentVariables(Set.of(endpointEnvVariable))
                .build();

        var endpoint = Endpoint.of(SchemesHelper.HTTP_SCHEME, WIREMOCK_HOST, WIREMOCK_MAPPED_PORT);

        var container = StubHelper.defaultStub(GenericContainer.class);
        var service = createWireMockService(config, container);

        givenContainerAccessible(container, containerAccessibility);
        givenNetworkConfigured(networkConfigurer, WIREMOCK_SERVICE_NAME, endpoint);

        // when
        service.start(ServiceStartupContext.defaultContext());

        // then
        var endpointPropertyEntry = ConfigurationEntry.property(endpointProperty, endpoint.address());
        var endpointEnvVariableEntry = ConfigurationEntry.envVariable(endpointEnvVariable, endpoint.address());
        var endpointEntries = List.<ConfigurationEntry>of(endpointPropertyEntry, endpointEnvVariableEntry);

        assertContainerStarted(container);
        assertContainerExposedPortConfigured(container, WIREMOCK_LISTENING_PORT);
        assertContainerWaitStrategyConfigured(container, Wait.forListeningPort());
        assertContainerLoggingConfigured(container);
        assertContainerNoEnvVariablesConfigured(container);
        assertName(service, WIREMOCK_SERVICE_NAME);
        assertSingleEndpoint(service, endpoint);
        assertNetworkControl(service);
        assertEntriesRegistered(service, endpointEntries);
        assertNetworkConfigured(networkConfigurer, WIREMOCK_SERVICE_NAME, endpoint);
    }

    @Test
    public void shouldShutdownService() {
        // given
        var config = WiremockDockerServiceConfig
                .builder()
                .name(WIREMOCK_SERVICE_NAME)
                .dockerImage(WIREMOCK_SERVICE_IMAGE)
                .scheme(SchemesHelper.HTTP_SCHEME)
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

        return new WiremockDockerService(container, config, configurationRegistry, endpointRegisterer, networkConfigurer);
    }

}
