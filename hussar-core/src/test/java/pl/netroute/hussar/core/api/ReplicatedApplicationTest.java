package pl.netroute.hussar.core.api;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ReplicatedApplicationTest {

    @Test
    public void shouldStartMultipleReplicas() {
        // given
        var replicaA = mock(Application.class);
        var replicaB = mock(Application.class);
        var replicas = List.of(replicaA, replicaB);

        // when
        ReplicatedApplication
                .newInstance(replicas)
                .start();

        // then
        assertReplicaStarted(replicaA);
        assertReplicaStarted(replicaB);
    }

    @Test
    public void shouldShutdownMultipleReplicas() {
        // given
        var replicaA = mock(Application.class);
        var replicaB = mock(Application.class);
        var replicas = List.of(replicaA, replicaB);

        // when
        ReplicatedApplication
                .newInstance(replicas)
                .shutdown();

        // then
        assertReplicaShutdown(replicaA);
        assertReplicaShutdown(replicaB);
    }

    private void assertReplicaStarted(Application replica) {
       verify(replica).start();
    }

    private void assertReplicaShutdown(Application replica) {
        verify(replica).shutdown();
    }
}
