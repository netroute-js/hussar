package com.netroute.hussar.service.wiremock.assertion;

import com.github.tomakehurst.wiremock.client.WireMock;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.hc.core5.http.NoHttpResponseException;
import pl.netroute.hussar.core.Endpoint;
import pl.netroute.hussar.core.api.ConfigurationEntry;
import pl.netroute.hussar.core.api.EnvVariableConfigurationEntry;
import pl.netroute.hussar.core.api.PropertyConfigurationEntry;
import pl.netroute.hussar.core.api.Service;
import pl.netroute.hussar.core.helper.EndpointHelper;

import java.net.SocketException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@RequiredArgsConstructor
public class WiremockAssertionHelper {
    private static final int SINGLE = 1;

    @NonNull
    private final Service wiremock;

    public void assertSingleEndpoint() {
        assertThat(wiremock.getEndpoints()).hasSize(SINGLE);
    }

    public void assertWiremockAccessible() {
        var endpoint = EndpointHelper.getAnyEndpointOrFail(wiremock);
        var wiremockClient = new WireMock(endpoint.getHost(), endpoint.getPort());

        wiremockClient.resetToDefaultMappings();
    }

    public void assertWiremockNotAccessible(@NonNull Endpoint endpoint) {
        var wiremockClient = new WireMock(endpoint.getHost(), endpoint.getPort());

        assertThatThrownBy(() -> wiremockClient.resetToDefaultMappings())
                .isInstanceOfAny(SocketException.class, NoHttpResponseException.class);
    }

    public void assertRegisteredEndpointUnderProperty(@NonNull String registeredProperty) {
        var endpoint = EndpointHelper.getAnyEndpointOrFail(wiremock);

        assertRegisteredEndpointInConfigRegistry(registeredProperty, endpoint.getAddress(), PropertyConfigurationEntry.class);
    }

    public void assertRegisteredEndpointUnderEnvironmentVariable(@NonNull String registeredEnvVariable) {
        var endpoint = EndpointHelper.getAnyEndpointOrFail(wiremock);

        assertRegisteredEndpointInConfigRegistry(registeredEnvVariable, endpoint.getAddress(), EnvVariableConfigurationEntry.class);
    }

    public void assertNoConfigurationsRegistered() {
        var entriesRegistered = wiremock
                .getConfigurationRegistry()
                .getEntries();

        assertThat(entriesRegistered).isEmpty();
    }

    private void assertRegisteredEndpointInConfigRegistry(String entryName, String entryValue, Class<? extends ConfigurationEntry> configType) {
        var endpoint = EndpointHelper.getAnyEndpointOrFail(wiremock);
        var configRegistry = wiremock.getConfigurationRegistry();

        configRegistry
                .getEntries()
                .stream()
                .filter(configEntry -> configEntry.getClass().equals(configType))
                .filter(configEntry -> configEntry.name().equals(entryName))
                .findFirst()
                .ifPresentOrElse(
                        configEntry -> assertThat(configEntry.value()).isEqualTo(entryValue),
                        () -> { throw new AssertionError("Expected registered endpoint in config registry. Found none"); }
                );
    }

}
