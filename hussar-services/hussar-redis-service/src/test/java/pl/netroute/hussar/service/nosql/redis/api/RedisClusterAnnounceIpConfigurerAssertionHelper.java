package pl.netroute.hussar.service.nosql.redis.api;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.testcontainers.containers.FixedHostPortGenericContainer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class RedisClusterAnnounceIpConfigurerAssertionHelper {

    static void assertClusterAnnounceIpConfigured(@NonNull RedisClusterAnnounceIpConfigurer clusterAnnounceIpConfigurer,
                                                  @NonNull String clusterAnnounceIp,
                                                  @NonNull FixedHostPortGenericContainer<?> container) {
        verify(clusterAnnounceIpConfigurer).configure(clusterAnnounceIp, container);
    }

    static void assertNoClusterAnnounceIpConfigured(@NonNull RedisClusterAnnounceIpConfigurer clusterAnnounceIpConfigurer) {
        verify(clusterAnnounceIpConfigurer, never()).configure(anyString(), any());
    }

}
