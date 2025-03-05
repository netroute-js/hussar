package pl.netroute.hussar.service.nosql.redis.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import pl.netroute.hussar.core.docker.DockerCommandLineRunner;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RedisClusterReplicationPasswordConfigurerTest {
    private static final String REDIS_HOST = "localhost";
    private static final int REDIS_FIRST_REPLICA_PORT = 7000;
    private static final int REDIS_SECOND_REPLICA_PORT = 7001;
    private static final String REDIS_PASSWORD = "a-password";

    private static final String CONFIGURE_REDIS_REPLICATION_PASSWORD_COMMAND = "redis-cli -h %s -p %d CONFIG SET masterauth %s";

    private DockerCommandLineRunner commandLineRunner;
    private RedisClusterReplicationPasswordConfigurer configurer;

    @BeforeEach
    public void setup() {
        commandLineRunner = mock(DockerCommandLineRunner.class);

        configurer = new RedisClusterReplicationPasswordConfigurer(commandLineRunner);
    }

    @Test
    public void shouldConfigureReplicationPassword() {
        // given
        var credentials = new RedisCredentials("a-username", "a-password");
        var container = createStubContainer();

        // when
        configurer.configure(credentials, container);

        // then
        var expectedFirstReplicaCommandExecuted = CONFIGURE_REDIS_REPLICATION_PASSWORD_COMMAND.formatted(REDIS_HOST, REDIS_FIRST_REPLICA_PORT, REDIS_PASSWORD);
        var expectedSecondReplicaCommandExecuted = CONFIGURE_REDIS_REPLICATION_PASSWORD_COMMAND.formatted(REDIS_HOST, REDIS_SECOND_REPLICA_PORT, REDIS_PASSWORD);

        assertReplicationPasswordConfigured(expectedFirstReplicaCommandExecuted, container);
        assertReplicationPasswordConfigured(expectedSecondReplicaCommandExecuted, container);
    }

    private GenericContainer<?> createStubContainer() {
        var container = mock(GenericContainer.class);
        when(container.getHost()).thenReturn(REDIS_HOST);
        when(container.getExposedPorts()).thenReturn(List.of(REDIS_FIRST_REPLICA_PORT, REDIS_SECOND_REPLICA_PORT));

        return container;
    }

    private void assertReplicationPasswordConfigured(String expectedCommandExecuted,
                                                     GenericContainer<?> container) {
        verify(commandLineRunner).run(expectedCommandExecuted, container);
    }

}
