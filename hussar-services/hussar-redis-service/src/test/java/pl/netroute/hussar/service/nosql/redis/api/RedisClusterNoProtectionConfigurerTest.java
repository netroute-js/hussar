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
import static pl.netroute.hussar.core.stub.GenericContainerStubHelper.givenContainerAccessible;

public class RedisClusterNoProtectionConfigurerTest {
    private static final String REDIS_HOST = "localhost";
    private static final int REDIS_FIRST_REPLICA_PORT = 7000;
    private static final int REDIS_SECOND_REPLICA_PORT = 7001;

    private static final String DISABLE_PROTECTION_MODE_COMMAND = "redis-cli -h %s -p %d CONFIG SET protected-mode no";

    private GenericContainerAccessibility containerAccessibility;
    private DockerCommandLineRunner commandLineRunner;
    private RedisClusterNoProtectionConfigurer configurer;

    @BeforeEach
    public void setup() {
        commandLineRunner = mock(DockerCommandLineRunner.class);

        containerAccessibility = GenericContainerAccessibility
                .builder()
                .host(REDIS_HOST)
                .exposedPorts(List.of(REDIS_FIRST_REPLICA_PORT, REDIS_SECOND_REPLICA_PORT))
                .build();

        configurer = new RedisClusterNoProtectionConfigurer(commandLineRunner);
    }

    @Test
    public void shouldConfigureNoProtectionMode() {
        // given
        var container = createStubFixedHostPortGenericContainer();

        givenContainerAccessible(container, containerAccessibility);

        // when
        configurer.configure(container);

        // then
        var expectedFirstReplicaCommandExecuted = DISABLE_PROTECTION_MODE_COMMAND.formatted(REDIS_HOST, REDIS_FIRST_REPLICA_PORT);
        var expectedSecondReplicaCommandExecuted = DISABLE_PROTECTION_MODE_COMMAND.formatted(REDIS_HOST, REDIS_SECOND_REPLICA_PORT);

        assertProtectionModeDisabled(expectedFirstReplicaCommandExecuted, container);
        assertProtectionModeDisabled(expectedSecondReplicaCommandExecuted, container);
    }

    private void assertProtectionModeDisabled(String expectedCommandExecuted,
                                              GenericContainer<?> container) {
        verify(commandLineRunner).run(expectedCommandExecuted, container);
    }

}
