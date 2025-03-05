package pl.netroute.hussar.service.nosql.redis.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import pl.netroute.hussar.core.docker.DockerCommandLineRunner;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RedisClusterAnnounceIpConfigurerTest {
    private static final String REDIS_HOST = "localhost";
    private static final int REDIS_FIRST_REPLICA_PORT = 7000;
    private static final int REDIS_SECOND_REPLICA_PORT = 7001;

    private static final String CONFIGURE_CLUSTER_ANNOUNCE_IP_COMMAND = "redis-cli -h %s -p %d CONFIG SET cluster-announce-ip %s";

    private DockerCommandLineRunner commandLineRunner;
    private RedisClusterAnnounceIpConfigurer configurer;

    @BeforeEach
    public void setup() {
        commandLineRunner = mock(DockerCommandLineRunner.class);

        configurer = new RedisClusterAnnounceIpConfigurer(commandLineRunner);
    }

    @Test
    public void shouldConfigureClusterAnnounceIp() {
        // given
        var clusterAnnounceIp = "docker";
        var container = createStubContainer();

        // when
        configurer.configure(clusterAnnounceIp, container);

        // then
        var expectedFirstReplicaCommandExecuted = CONFIGURE_CLUSTER_ANNOUNCE_IP_COMMAND.formatted(REDIS_HOST, REDIS_FIRST_REPLICA_PORT, clusterAnnounceIp);
        var expectedSecondReplicaCommandExecuted = CONFIGURE_CLUSTER_ANNOUNCE_IP_COMMAND.formatted(REDIS_HOST, REDIS_SECOND_REPLICA_PORT, clusterAnnounceIp);

        assertClusterAnnounceIpConfigured(expectedFirstReplicaCommandExecuted, container);
        assertClusterAnnounceIpConfigured(expectedSecondReplicaCommandExecuted, container);
    }

    private GenericContainer<?> createStubContainer() {
        var container = mock(GenericContainer.class);
        when(container.getHost()).thenReturn(REDIS_HOST);
        when(container.getExposedPorts()).thenReturn(List.of(REDIS_FIRST_REPLICA_PORT, REDIS_SECOND_REPLICA_PORT));

        return container;
    }

    private void assertClusterAnnounceIpConfigured(String expectedCommandExecuted,
                                                   GenericContainer<?> container) {
        verify(commandLineRunner).run(expectedCommandExecuted, container);
    }

}
