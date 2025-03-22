package pl.netroute.hussar.service.rabbitmq.api;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class RabbitMQDockerServiceConfigTest {

    @Test
    public void shouldFailBuildingWhenManagementApiMissingAndPropertiesRegistrationRequired() {
        // given
        var managementEndpointProperty = "management.endpoint.url";

        // when
        // then
        assertThatThrownBy(() ->
                RabbitMQDockerServiceConfig
                        .builder()
                        .name("some-name")
                        .dockerImage("some-docker")
                        .queues(Set.of())
                        .registerEndpointUnderProperties(Set.of())
                        .registerEndpointUnderEnvironmentVariables(Set.of())
                        .registerUsernameUnderProperties(Set.of())
                        .registerUsernameUnderEnvironmentVariables(Set.of())
                        .registerPasswordUnderProperties(Set.of())
                        .registerPasswordUnderEnvironmentVariables(Set.of())
                        .registerManagementEndpointUnderProperties(Set.of(managementEndpointProperty))
                        .registerManagementEndpointUnderEnvironmentVariables(Set.of())
                        .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Management API is disabled but Management API registration is required");
    }

    @Test
    public void shouldFailBuildingWhenManagementApiMissingAndEnvVariableRegistrationRequired() {
        // given
        var managementEndpointEnvVariable = "MANAGEMENT_ENDPOINT_URL";

        // when
        // then
        assertThatThrownBy(() ->
                RabbitMQDockerServiceConfig
                        .builder()
                        .name("some-name")
                        .dockerImage("some-docker")
                        .queues(Set.of())
                        .registerEndpointUnderProperties(Set.of())
                        .registerEndpointUnderEnvironmentVariables(Set.of())
                        .registerUsernameUnderProperties(Set.of())
                        .registerUsernameUnderEnvironmentVariables(Set.of())
                        .registerPasswordUnderProperties(Set.of())
                        .registerPasswordUnderEnvironmentVariables(Set.of())
                        .registerManagementEndpointUnderProperties(Set.of())
                        .registerManagementEndpointUnderEnvironmentVariables(Set.of(managementEndpointEnvVariable))
                        .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Management API is disabled but Management API registration is required");
    }

}
