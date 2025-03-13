package pl.netroute.hussar.service.nosql.redis.api;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.testcontainers.containers.FixedHostPortGenericContainer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class RedisClusterReplicationPasswordConfigurerAssertionHelper {

    static void assertReplicationPasswordConfigured(@NonNull RedisClusterReplicationPasswordConfigurer clusterReplicationPasswordConfigurer,
                                                    @NonNull RedisCredentials credentials,
                                                    @NonNull FixedHostPortGenericContainer<?> container) {
        verify(clusterReplicationPasswordConfigurer).configure(credentials, container);
    }

    static void assertNoReplicationPasswordConfigured(@NonNull RedisClusterReplicationPasswordConfigurer clusterReplicationPasswordConfigurer) {
        verify(clusterReplicationPasswordConfigurer, never()).configure(any(), any());
    }

}
