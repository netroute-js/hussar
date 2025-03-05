package pl.netroute.hussar.core.service.registerer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.netroute.hussar.core.api.Endpoint;
import pl.netroute.hussar.core.configuration.api.ConfigurationEntry;
import pl.netroute.hussar.core.configuration.api.ConfigurationRegistry;
import pl.netroute.hussar.core.configuration.api.EnvVariableConfigurationEntry;
import pl.netroute.hussar.core.configuration.api.DefaultConfigurationRegistry;
import pl.netroute.hussar.core.configuration.api.PropertyConfigurationEntry;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.netroute.hussar.core.service.registerer.EndpointRegisterer.JOIN_DELIMITER;

public class EndpointRegistererTest {
    private ConfigurationRegistry configurationRegistry;
    private EndpointRegisterer endpointRegisterer;

    @BeforeEach
    public void setup() {
        configurationRegistry = new DefaultConfigurationRegistry();

        endpointRegisterer = new EndpointRegisterer(configurationRegistry);
    }

    @Test
    public void shouldRegisterSingleEndpointUnderProperty() {
        // given
        var endpoint = Endpoint.schemeLess("localhost", 8080);
        var endpointProperty = "a.property";

        // when
        endpointRegisterer.registerUnderProperty(List.of(endpoint), endpointProperty);

        // then
        var registeredEndpointProperty = new PropertyConfigurationEntry(endpointProperty, endpoint.address());
        var registeredProperties = Set.of(registeredEndpointProperty);

        assertRegisteredConfigs(registeredProperties);
    }

    @Test
    public void shouldRegisterMultipleEndpointsUnderProperty() {
        // given
        var firstEndpoint = Endpoint.schemeLess("localhost", 8080);
        var secondEndpoint = Endpoint.schemeLess("localhost", 8081);
        var endpoints = List.of(firstEndpoint, secondEndpoint);

        var endpointProperty = "a.property";

        // when
        endpointRegisterer.registerUnderProperty(endpoints, endpointProperty);

        // then
        var formattedEndpoints = formatEndpoints(endpoints);
        var registeredEndpointProperty = new PropertyConfigurationEntry(endpointProperty, formattedEndpoints);
        var registeredProperties = Set.of(registeredEndpointProperty);

        assertRegisteredConfigs(registeredProperties);
    }

    @Test
    public void shouldRegisterSingleEndpointUnderEnvironmentVariables() {
        // given
        var endpoint = Endpoint.schemeLess("localhost", 8080);
        var endpointEnvVariable = "ENV_VARIABLE_A";

        // when
        endpointRegisterer.registerUnderEnvironmentVariable(List.of(endpoint), endpointEnvVariable);

        // then
        var registeredEndpointEnvVariable = new EnvVariableConfigurationEntry(endpointEnvVariable, endpoint.address());
        var registeredEnvVariables = Set.of(registeredEndpointEnvVariable);

        assertRegisteredConfigs(registeredEnvVariables);
    }

    @Test
    public void shouldRegisterMultipleEndpointsUnderEnvironmentVariables() {
        // given
        var firstEndpoint = Endpoint.schemeLess("localhost", 8080);
        var secondEndpoint = Endpoint.schemeLess("localhost", 8081);
        var endpoints = List.of(firstEndpoint, secondEndpoint);

        var endpointEnvVariable = "ENV_VARIABLE_A";

        // when
        endpointRegisterer.registerUnderEnvironmentVariable(endpoints, endpointEnvVariable);

        // then
        var formattedEndpoints = formatEndpoints(endpoints);
        var registeredEndpointEnvVariable = new EnvVariableConfigurationEntry(endpointEnvVariable, formattedEndpoints);
        var registeredEnvVariables = Set.of(registeredEndpointEnvVariable);

        assertRegisteredConfigs(registeredEnvVariables);
    }

    private String formatEndpoints(List<Endpoint> endpoints) {
        return endpoints
                .stream()
                .map(Endpoint::address)
                .collect(Collectors.joining(JOIN_DELIMITER));
    }

    private void assertRegisteredConfigs(Set<? extends ConfigurationEntry> registeredEntries) {
        assertThat(configurationRegistry.getEntries()).containsExactlyInAnyOrderElementsOf(registeredEntries);
    }

}
