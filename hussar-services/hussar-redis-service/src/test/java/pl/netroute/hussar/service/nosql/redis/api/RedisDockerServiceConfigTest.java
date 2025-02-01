package pl.netroute.hussar.service.nosql.redis.api;

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
                    .registerUsernameUnderProperties(Set.of())
                    .registerUsernameUnderEnvironmentVariables(Set.of())
                    .registerPasswordUnderProperties(Set.of(passwordProperty))
                    .registerPasswordUnderEnvironmentVariables(Set.of())
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
                        .registerUsernameUnderProperties(Set.of())
                        .registerUsernameUnderEnvironmentVariables(Set.of())
                        .registerPasswordUnderProperties(Set.of())
                        .registerPasswordUnderEnvironmentVariables(Set.of(passwordEnvVariable))
                        .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Redis password is disabled but password registration is required");

    }

}
