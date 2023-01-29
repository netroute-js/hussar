package com.netroute.hussar.wiremock;

import com.github.tomakehurst.wiremock.client.WireMock;
import org.apache.hc.client5.http.HttpHostConnectException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import pl.netroute.hussar.core.Endpoint;
import pl.netroute.hussar.core.helper.PropertiesHelper;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class WiremockServiceTest {
    private WiremockService wiremockService;

    @AfterEach
    public void cleanup() {
        Optional
                .ofNullable(wiremockService)
                .ifPresent(WiremockService::shutdown);
    }

    @Test
    public void shouldStartWiremockService() {
        // given
        wiremockService = WiremockServiceConfigurer
                .newInstance()
                .configure();

        // when
        wiremockService.start();

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
        var endpointPropertyA = "propertyA.wiremock.url";
        var endpointPropertyB = "propertyB.wiremock.url";

        wiremockService = WiremockServiceConfigurer
                .newInstance()
                .name(name)
                .dockerImageVersion(dockerVersion)
                .registerEndpointUnderProperty(endpointPropertyA)
                .registerEndpointUnderProperty(endpointPropertyB)
                .configure();

        // when
        wiremockService.start();

        // then
        var endpoints = wiremockService.getEndpoints();
        assertSingleEndpoint(endpoints);

        var endpoint = endpoints.get(0);
        assertWiremockAccessible(endpoint);
        assertRegisteredEndpointUnderProperty(endpointPropertyA, endpoint);
        assertRegisteredEndpointUnderProperty(endpointPropertyB, endpoint);
    }

    @Test
    public void shouldShutdownWiremockService() {
        // given
        var name = "wiremock-instance";
        var dockerVersion = "2.34.0";
        var endpointPropertyA = "propertyA.wiremock.url";
        var endpointPropertyB = "propertyB.wiremock.url";

        wiremockService = WiremockServiceConfigurer
                .newInstance()
                .name(name)
                .dockerImageVersion(dockerVersion)
                .registerEndpointUnderProperty(endpointPropertyA)
                .registerEndpointUnderProperty(endpointPropertyB)
                .configure();

        // when
        wiremockService.start();

        var endpoints = wiremockService.getEndpoints();

        wiremockService.shutdown();

        // then
        assertSingleEndpoint(endpoints);

        var endpoint = endpoints.get(0);
        assertWiremockNotAccessible(endpoint);
        assertPropertyNotSet(endpointPropertyA);
        assertPropertyNotSet(endpointPropertyB);
    }

    private void assertSingleEndpoint(List<Endpoint> endpoints) {
        assertThat(endpoints).hasSize(1);
    }

    private void assertRegisteredEndpointUnderProperty(String property, Endpoint endpoint) {
        assertThat(PropertiesHelper.getProperty(property)).hasValue(endpoint.getAddress());
    }

    private void assertPropertyNotSet(String property) {
        assertThat(PropertiesHelper.getProperty(property)).isEmpty();
    }

    private void assertWiremockAccessible(Endpoint endpoint) {
        var wiremockClient = new WireMock(endpoint.getHost(), endpoint.getPort());

        wiremockClient.resetToDefaultMappings();
    }

    private void assertWiremockNotAccessible(Endpoint endpoint) {
        var wiremockClient = new WireMock(endpoint.getHost(), endpoint.getPort());

        assertThatThrownBy(() -> wiremockClient.resetToDefaultMappings())
                .isInstanceOf(HttpHostConnectException.class)
                .hasMessageContaining("failed: Connection refused: connect");
    }

}
