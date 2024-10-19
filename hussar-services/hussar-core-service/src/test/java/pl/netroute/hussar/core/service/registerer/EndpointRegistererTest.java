package pl.netroute.hussar.core.service.registerer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.netroute.hussar.core.api.Endpoint;
import pl.netroute.hussar.core.api.configuration.ConfigurationEntry;
import pl.netroute.hussar.core.api.configuration.ConfigurationRegistry;
import pl.netroute.hussar.core.api.configuration.EnvVariableConfigurationEntry;
import pl.netroute.hussar.core.api.configuration.DefaultConfigurationRegistry;
import pl.netroute.hussar.core.api.configuration.PropertyConfigurationEntry;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class EndpointRegistererTest {
    private ConfigurationRegistry configurationRegistry;
    private EndpointRegisterer endpointRegisterer;

    @BeforeEach
    public void setup() {
        configurationRegistry = new DefaultConfigurationRegistry();

        endpointRegisterer = new EndpointRegisterer(configurationRegistry);
    }

    @Test
    public void shouldRegisterEndpointUnderProperty() {
        // given
        var endpoint = Endpoint.schemeLess("localhost", 8080);

        var endpointProperty = "a.property";

        // when
        endpointRegisterer.registerUnderProperty(endpoint, endpointProperty);

        // then
        var registeredEndpointProperty = new PropertyConfigurationEntry(endpointProperty, endpoint.address());
        var registeredProperties = Set.of(registeredEndpointProperty);

        assertRegisteredConfigs(registeredProperties);
    }

    @Test
    public void shouldRegisterEndpointUnderEnvironmentVariables() {
        // given
        var endpoint = Endpoint.schemeLess("localhost", 8080);

        var endpointEnvVariable = "ENV_VARIABLE_A";

        // when
        endpointRegisterer.registerUnderEnvironmentVariable(endpoint, endpointEnvVariable);

        // then
        var registeredEndpointEnvVariable = new EnvVariableConfigurationEntry(endpointEnvVariable, endpoint.address());
        var registeredEnvVariables = Set.of(registeredEndpointEnvVariable);

        assertRegisteredConfigs(registeredEnvVariables);
    }

    private void assertRegisteredConfigs(Set<? extends ConfigurationEntry> registeredEntries) {
        assertThat(configurationRegistry.getEntries()).containsExactlyInAnyOrderElementsOf(registeredEntries);
    }

}
