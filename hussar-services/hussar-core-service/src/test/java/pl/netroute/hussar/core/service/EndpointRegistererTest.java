package pl.netroute.hussar.core.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.netroute.hussar.core.Endpoint;
import pl.netroute.hussar.core.api.ConfigurationEntry;
import pl.netroute.hussar.core.api.ConfigurationRegistry;
import pl.netroute.hussar.core.api.EnvVariableConfigurationEntry;
import pl.netroute.hussar.core.api.MapConfigurationRegistry;
import pl.netroute.hussar.core.api.PropertyConfigurationEntry;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class EndpointRegistererTest {
    private ConfigurationRegistry configurationRegistry;
    private EndpointRegisterer endpointRegisterer;

    @BeforeEach
    public void setup() {
        configurationRegistry = new MapConfigurationRegistry();

        endpointRegisterer = new EndpointRegisterer(configurationRegistry);
    }

    @Test
    public void shouldRegisterEndpointUnderProperty() {
        // given
        var endpoint = Endpoint.of("localhost", 8080);

        var endpointProperty = "a.property";

        // when
        endpointRegisterer.registerUnderProperty(endpoint, endpointProperty);

        // then
        var registeredEndpointProperty = new PropertyConfigurationEntry(endpointProperty, endpoint.getAddress());
        var registeredProperties = Set.of(registeredEndpointProperty);

        assertRegisteredConfigs(registeredProperties);
    }

    @Test
    public void shouldRegisterEndpointUnderEnvironmentVariables() {
        // given
        var endpoint = Endpoint.of("localhost", 8080);

        var endpointEnvVariable = "ENV_VARIABLE_A";

        // when
        endpointRegisterer.registerUnderEnvironmentVariable(endpoint, endpointEnvVariable);

        // then
        var registeredEndpointEnvVariable = new EnvVariableConfigurationEntry(endpointEnvVariable, endpoint.getAddress());
        var registeredEnvVariables = Set.of(registeredEndpointEnvVariable);

        assertRegisteredConfigs(registeredEnvVariables);
    }

    private void assertRegisteredConfigs(Set<? extends ConfigurationEntry> registeredEntries) {
        assertThat(configurationRegistry.getEntries()).containsExactlyInAnyOrderElementsOf(registeredEntries);
    }

}
