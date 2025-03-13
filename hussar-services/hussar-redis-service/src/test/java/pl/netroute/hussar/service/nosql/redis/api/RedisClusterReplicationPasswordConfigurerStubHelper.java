package pl.netroute.hussar.service.nosql.redis.api;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.testcontainers.containers.FixedHostPortGenericContainer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class RedisClusterReplicationPasswordConfigurerStubHelper {

    static void givenReplicationPasswordConfigurationFails(@NonNull RedisClusterReplicationPasswordConfigurer clusterReplicationPasswordConfigurer,
                                                           @NonNull FixedHostPortGenericContainer<?> container) {
        var failure = new IllegalStateException("Docker command has failed with [-1] code");

        doThrow(failure)
                .when(clusterReplicationPasswordConfigurer)
                .configure(any(), eq(container));
    }

}
