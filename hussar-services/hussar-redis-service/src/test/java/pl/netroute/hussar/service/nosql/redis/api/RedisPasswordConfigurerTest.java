package pl.netroute.hussar.service.nosql.redis.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import pl.netroute.hussar.core.docker.DockerCommandLineRunner;
import pl.netroute.hussar.core.stub.GenericContainerStubHelper.GenericContainerAccessibility;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static pl.netroute.hussar.core.stub.GenericContainerStubHelper.createStubFixedHostPortGenericContainer;
import static pl.netroute.hussar.core.stub.GenericContainerStubHelper.createStubGenericContainer;
import static pl.netroute.hussar.core.stub.GenericContainerStubHelper.givenContainerAccessible;

public class RedisPasswordConfigurerTest {
    private static final String REDIS_HOST = "localhost";
    private static final int REDIS_PORT = 7000;

    private static final String CONFIGURE_PASSWORD_COMMAND = "redis-cli -h %s -p %d CONFIG SET requirepass %s";

    private GenericContainerAccessibility containerAccessibility;
    private DockerCommandLineRunner commandLineRunner;
    private RedisPasswordConfigurer configurer;

    @BeforeEach
    public void setup() {
        commandLineRunner = mock(DockerCommandLineRunner.class);

        containerAccessibility = GenericContainerAccessibility
                .builder()
                .host(REDIS_HOST)
                .exposedPorts(List.of(REDIS_PORT))
                .build();

        configurer = new RedisPasswordConfigurer(commandLineRunner);
    }

    @Test
    public void shouldConfigurePasswordInGenericContainer() {
        // given
        var credentials = new RedisCredentials("default", "a-password");
        var container = createStubGenericContainer();

        givenContainerAccessible(container, containerAccessibility);

        // when
        configurer.configure(credentials, container);

        // then
        var expectedCommandExecuted = CONFIGURE_PASSWORD_COMMAND.formatted(REDIS_HOST, REDIS_PORT, credentials.password());

        assertPasswordConfigured(expectedCommandExecuted, container);
    }

    @Test
    public void shouldConfigurePasswordInFixedHostContainer() {
        // given
        var credentials = new RedisCredentials("default", "a-password");
        var container = createStubFixedHostPortGenericContainer();

        givenContainerAccessible(container, containerAccessibility);

        // when
        configurer.configure(credentials, container);

        // then
        var expectedCommandExecuted = CONFIGURE_PASSWORD_COMMAND.formatted(REDIS_HOST, REDIS_PORT, credentials.password());

        assertPasswordConfigured(expectedCommandExecuted, container);
    }

    private void assertPasswordConfigured(String expectedCommandExecuted,
                                          GenericContainer<?> container) {
        verify(commandLineRunner).run(expectedCommandExecuted, container);
    }

}
