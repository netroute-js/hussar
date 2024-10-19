package pl.netroute.hussar.service.wiremock.assertion;

import com.github.tomakehurst.wiremock.client.WireMock;
import pl.netroute.hussar.service.wiremock.WiremockDockerService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.hc.core5.http.NoHttpResponseException;
import pl.netroute.hussar.core.api.Endpoint;
import pl.netroute.hussar.core.api.configuration.ConfigurationEntry;
import pl.netroute.hussar.core.api.configuration.EnvVariableConfigurationEntry;
import pl.netroute.hussar.core.api.configuration.PropertyConfigurationEntry;
import pl.netroute.hussar.core.helper.EndpointHelper;

import java.net.SocketException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@RequiredArgsConstructor
public class WiremockAssertionHelper {
    private static final int SINGLE = 1;

    @NonNull
    private final WiremockDockerService wiremock;

    public void assertSingleEndpoint() {
        assertThat(wiremock.getEndpoints()).hasSize(SINGLE);
    }

    public void assertWiremockAccessible() {
        var endpoint = EndpointHelper.getAnyEndpointOrFail(wiremock);
        var wiremockClient = new WireMock(endpoint.host(), endpoint.port());

        wiremockClient.resetToDefaultMappings();
    }

    public void assertWiremockNotAccessible(@NonNull Endpoint endpoint) {
        var wiremockClient = new WireMock(endpoint.host(), endpoint.port());

        assertThatThrownBy(wiremockClient::resetToDefaultMappings)
                .isInstanceOfAny(SocketException.class, NoHttpResponseException.class);
    }

    public void assertRegisteredEndpointUnderProperty(@NonNull String registeredProperty) {
        var endpoint = EndpointHelper.getAnyEndpointOrFail(wiremock);

        assertRegisteredEndpointInConfigRegistry(registeredProperty, endpoint.address(), PropertyConfigurationEntry.class);
    }

    public void assertRegisteredEndpointUnderEnvironmentVariable(@NonNull String registeredEnvVariable) {
        var endpoint = EndpointHelper.getAnyEndpointOrFail(wiremock);

        assertRegisteredEndpointInConfigRegistry(registeredEnvVariable, endpoint.address(), EnvVariableConfigurationEntry.class);
    }

    public void assertNoConfigurationsRegistered() {
        var entriesRegistered = wiremock
                .getConfigurationRegistry()
                .getEntries();

        assertThat(entriesRegistered).isEmpty();
    }

    private void assertRegisteredEndpointInConfigRegistry(String entryName, String entryValue, Class<? extends ConfigurationEntry> configType) {
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
