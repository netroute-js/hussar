package com.netroute.hussar.service.wiremock;

import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import pl.netroute.hussar.core.Endpoint;
import pl.netroute.hussar.core.api.ConfigurationRegistry;
import pl.netroute.hussar.core.api.ServiceStartupContext;
import pl.netroute.hussar.core.service.api.RegistrableConfigurationEntry;

import java.net.SocketException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class WiremockDockerServiceTest {
    private WiremockDockerService wiremockService;

    @AfterEach
    public void cleanup() {
        Optional
                .ofNullable(wiremockService)
                .ifPresent(WiremockDockerService::shutdown);
    }

    @Test
    public void shouldStartWiremockService() {
        // given
        wiremockService = WiremockDockerServiceConfigurer
                .newInstance()
                .configure();

        // when
        wiremockService.start(ServiceStartupContext.empty());

        // then
        var endpoints = wiremockService.getEndpoints();
        assertSingleEndpoint(endpoints);

        var endpoint = endpoints.get(0);
        assertWiremockAccessible(endpoint);
    }

    @Test
    public void shouldStartWiremockServiceWithFullConfiguration() {
        // given
        var name = "wiremock-instance";
        var dockerVersion = "2.34.0";
        var endpointProperty = "propertyA.wiremock.url";
        var endpointEnvVariable = "WIREMOCK_URL";

        wiremockService = WiremockDockerServiceConfigurer
                .newInstance()
                .name(name)
                .dockerImageVersion(dockerVersion)
                .registerEndpointUnderEntry(RegistrableConfigurationEntry.property(endpointProperty))
                .registerEndpointUnderEntry(RegistrableConfigurationEntry.envVariable(endpointEnvVariable))
                .configure();

        // when
        wiremockService.start(ServiceStartupContext.empty());

        // then
        var endpoints = wiremockService.getEndpoints();
        assertSingleEndpoint(endpoints);

        var endpoint = endpoints.get(0);
        assertWiremockAccessible(endpoint);

        var configRegistry = wiremockService.getConfigurationRegistry();
        assertRegisteredEndpointInConfigRegistry(endpointProperty, configRegistry, endpoint);
        assertRegisteredEndpointInConfigRegistry(endpointEnvVariable, configRegistry, endpoint);
    }

    @Test
    public void shouldShutdownWiremockService() {
        // given
        var name = "wiremock-instance";
        var dockerVersion = "2.34.0";

        wiremockService = WiremockDockerServiceConfigurer
                .newInstance()
                .name(name)
                .dockerImageVersion(dockerVersion)
                .configure();

        // when
        wiremockService.start(ServiceStartupContext.empty());

        var endpoints = wiremockService.getEndpoints();

        wiremockService.shutdown();

        // then
        assertSingleEndpoint(endpoints);

        var endpoint = endpoints.get(0);
        assertWiremockNotAccessible(endpoint);
    }

    private void assertSingleEndpoint(List<Endpoint> endpoints) {
        assertThat(endpoints).hasSize(1);
    }

    private void assertRegisteredEndpointInConfigRegistry(String registeredEntryName, ConfigurationRegistry registry, Endpoint endpoint) {
        registry
                .getEntries()
                .stream()
                .filter(configEntry -> configEntry.getName().equals(registeredEntryName))
                .findFirst()
                .ifPresentOrElse(
                        configEntry -> assertThat(configEntry.getValue()).isEqualTo(endpoint.getAddress()),
                        () -> { throw new AssertionError("Expected registered endpoint in config registry. Found none"); }
                );
    }

    private void assertWiremockAccessible(Endpoint endpoint) {
        var wiremockClient = new WireMock(endpoint.getHost(), endpoint.getPort());

        wiremockClient.resetToDefaultMappings();
    }

    private void assertWiremockNotAccessible(Endpoint endpoint) {
        var wiremockClient = new WireMock(endpoint.getHost(), endpoint.getPort());

        assertThatThrownBy(() -> wiremockClient.resetToDefaultMappings())
                .isInstanceOf(SocketException.class)
                .hasMessageContaining("Connection reset");
    }

}
