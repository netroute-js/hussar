package pl.netroute.hussar.service.nosql.redis.api;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rnorth.ducttape.unreliables.Unreliables;
import org.testcontainers.containers.wait.strategy.AbstractWaitStrategy;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.containers.wait.strategy.WaitStrategyTarget;
import pl.netroute.hussar.core.api.InternalUseOnly;
import pl.netroute.hussar.core.docker.DockerCommandLineRunner;

import java.util.concurrent.TimeUnit;

@Slf4j
@InternalUseOnly
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class RedisClusterWaitStrategy extends AbstractWaitStrategy {

    @NonNull
    private final DockerCommandLineRunner commandLineRunner;

    @Override
    protected void waitUntilReady() {
        var timeout = (int) startupTimeout.getSeconds();

        Unreliables.retryUntilTrue(timeout, TimeUnit.SECONDS, this::isClusterReady);
    }

    private boolean isClusterReady() {
        Wait.forListeningPort().waitUntilReady(waitStrategyTarget);

        var clusterHealthyReadinessProbe = new ClusterHealthReadinessProbe(waitStrategyTarget, commandLineRunner);

        return clusterHealthyReadinessProbe.isReady();
    }

    private interface ReadinessProbe {
        boolean isReady();
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static class ClusterHealthReadinessProbe implements ReadinessProbe {
        private static final String CLUSTER_INFO_COMMAND = "redis-cli -h %s -p %d cluster info";
        private static final String CLUSTER_STATE_OK = "cluster_state:ok";

        private static final int SUCCESSFUL_COMMAND_CODE = 0;

        private final WaitStrategyTarget waitTarget;
        private final DockerCommandLineRunner commandLineRunner;

        @Override
        public boolean isReady() {
            try {
                var host = waitTarget.getHost();
                var port = getRandomClusterNodePort();
                var command = CLUSTER_INFO_COMMAND.formatted(host, port);
                
                var result = commandLineRunner.runAndReturn(command, waitTarget);

                return result.getExitCode() == SUCCESSFUL_COMMAND_CODE && result.getStdout().contains(CLUSTER_STATE_OK);
            } catch (Exception ex) {
                log.error("RedisCluster readiness probe failed", ex);

                return false;
            }
        }

        private int getRandomClusterNodePort() {
            return waitTarget.getBoundPortNumbers().getFirst();
        }

    }

}
