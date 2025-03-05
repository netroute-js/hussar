package pl.netroute.hussar.service.nosql.redis.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import pl.netroute.hussar.core.docker.DockerCommandLineRunner;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RedisPasswordConfigurerTest {
    private static final String REDIS_HOST = "localhost";
    private static final int REDIS_PORT = 7000;

    private static final String CONFIGURE_PASSWORD_COMMAND = "redis-cli -h %s -p %d CONFIG SET requirepass %s";

    private DockerCommandLineRunner commandLineRunner;
    private RedisPasswordConfigurer configurer;

    @BeforeEach
    public void setup() {
        commandLineRunner = mock(DockerCommandLineRunner.class);

        configurer = new RedisPasswordConfigurer(commandLineRunner);
    }

    @Test
    public void shouldConfigurePassword() {
        // given
        var credentials = new RedisCredentials("default", "a-password");
        var container = createStubContainer();

        // when
        configurer.configure(credentials, container);

        // then
        var expectedCommandExecuted = CONFIGURE_PASSWORD_COMMAND.formatted(REDIS_HOST, REDIS_PORT, credentials.password());

        assertPasswordConfigured(expectedCommandExecuted, container);
    }

    private GenericContainer<?> createStubContainer() {
        var container = mock(GenericContainer.class);
        when(container.getHost()).thenReturn(REDIS_HOST);
        when(container.getExposedPorts()).thenReturn(List.of(REDIS_PORT));

        return container;
    }

    private void assertPasswordConfigured(String expectedCommandExecuted,
                                          GenericContainer<?> container) {
        verify(commandLineRunner).run(expectedCommandExecuted, container);
    }

}
