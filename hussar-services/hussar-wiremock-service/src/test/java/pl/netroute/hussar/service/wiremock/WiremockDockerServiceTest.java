package pl.netroute.hussar.service.wiremock;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import pl.netroute.hussar.core.api.Endpoint;
import pl.netroute.hussar.core.configuration.api.ConfigurationEntry;
import pl.netroute.hussar.core.configuration.api.DefaultConfigurationRegistry;
import pl.netroute.hussar.core.service.api.ServiceStartupContext;
import pl.netroute.hussar.core.helper.SchemesHelper;
import pl.netroute.hussar.core.service.registerer.EndpointRegisterer;

import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static pl.netroute.hussar.core.service.assertion.ServiceAssertionHelper.assertEntriesRegistered;
import static pl.netroute.hussar.core.service.assertion.ServiceAssertionHelper.assertName;
import static pl.netroute.hussar.core.service.assertion.ServiceAssertionHelper.assertNoEntriesRegistered;
import static pl.netroute.hussar.core.service.assertion.ServiceAssertionHelper.assertSingleEndpoint;
import static pl.netroute.hussar.core.service.assertion.GenericContainerAssertionHelper.assertContainerExposedPortConfigured;
import static pl.netroute.hussar.core.service.assertion.GenericContainerAssertionHelper.assertContainerLoggingConfigured;
import static pl.netroute.hussar.core.service.assertion.GenericContainerAssertionHelper.assertContainerWaitStrategyConfigured;
import static pl.netroute.hussar.core.service.assertion.GenericContainerAssertionHelper.assertContainerStarted;
import static pl.netroute.hussar.core.service.assertion.GenericContainerAssertionHelper.assertContainerStopped;
import static pl.netroute.hussar.core.service.assertion.GenericContainerAssertionHelper.assertContainerNoEnvVariablesConfigured;

public class WiremockDockerServiceTest {
    private static final String WIREMOCK_HOST = "localhost";
    private static final int WIREMOCK_LISTENING_PORT = 8080;
    private static final int WIREMOCK_MAPPED_PORT = 9080;

    private static final String WIREMOCK_SERVICE_NAME = "wiremock-service";
    private static final String WIREMOCK_SERVICE_IMAGE = "wiremock/wiremock";

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

        var container = createStubContainer();
        var service = createWireMockService(config, container);

        givenContainerAccessible(container);

        // when
        service.start(ServiceStartupContext.defaultContext());

        // then
        var endpoint = Endpoint.of(SchemesHelper.HTTP_SCHEME, WIREMOCK_HOST, WIREMOCK_MAPPED_PORT);

        assertContainerStarted(container);
        assertContainerExposedPortConfigured(container, WIREMOCK_LISTENING_PORT);
        assertContainerWaitStrategyConfigured(container, Wait.forListeningPort());
        assertContainerLoggingConfigured(container);
        assertContainerNoEnvVariablesConfigured(container);
        assertName(service, WIREMOCK_SERVICE_NAME);
        assertSingleEndpoint(service, endpoint);
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
                .scheme(SchemesHelper.HTTP_SCHEME)
                .registerEndpointUnderProperties(Set.of(endpointProperty))
                .registerEndpointUnderEnvironmentVariables(Set.of(endpointEnvVariable))
                .build();

        var container = createStubContainer();
        var service = createWireMockService(config, container);

        givenContainerAccessible(container);

        // when
        service.start(ServiceStartupContext.defaultContext());

        // then
        var endpoint = Endpoint.of(SchemesHelper.HTTP_SCHEME, WIREMOCK_HOST, WIREMOCK_MAPPED_PORT);
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
        assertEntriesRegistered(service, endpointEntries);
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

        var container = createStubContainer();
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

        return new WiremockDockerService(container, config, configurationRegistry, endpointRegisterer);
    }

    private GenericContainer<?> createStubContainer() {
        return mock(GenericContainer.class, RETURNS_DEEP_STUBS);
    }

    private void givenContainerAccessible(GenericContainer<?> container) {
        when(container.getHost()).thenReturn(WIREMOCK_HOST);
        when(container.getExposedPorts()).thenReturn(List.of(WIREMOCK_LISTENING_PORT));
        when(container.getMappedPort(WIREMOCK_LISTENING_PORT)).thenReturn(WIREMOCK_MAPPED_PORT);
    }
}
