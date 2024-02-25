package pl.netroute.hussar.service.nosql.redis;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class RedisDockerServiceConfigTest {

    @Test
    public void shouldFailBuildingWhenInvalidPasswordProperty() {
        // given
        var passwordProperty = "password.property";

        // when
        // then
        assertThatThrownBy(() ->
                RedisDockerServiceConfig
                    .builder()
                    .name("some-name")
                    .dockerImage("some-docker")
                    .enablePassword(false)
                    .registerEndpointUnderProperties(Set.of())
                    .registerEndpointUnderEnvironmentVariables(Set.of())
                    .registerPasswordUnderProperty(passwordProperty)
                    .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Redis password is disabled but password registration is required");

    }

    @Test
    public void shouldFailBuildingWhenInvalidPasswordEnvironmentVariable() {
        // given
        var passwordEnvVariable = "PASSWORD";

        // when
        // then
        assertThatThrownBy(() ->
                RedisDockerServiceConfig
                        .builder()
                        .name("some-name")
                        .dockerImage("some-docker")
                        .enablePassword(false)
                        .registerEndpointUnderProperties(Set.of())
                        .registerEndpointUnderEnvironmentVariables(Set.of())
                        .registerPasswordUnderEnvironmentVariable(passwordEnvVariable)
                        .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Redis password is disabled but password registration is required");

    }

}
