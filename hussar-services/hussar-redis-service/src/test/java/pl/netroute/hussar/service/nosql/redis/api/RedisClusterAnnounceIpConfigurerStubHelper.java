package pl.netroute.hussar.service.nosql.redis.api;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.testcontainers.containers.GenericContainer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class RedisClusterAnnounceIpConfigurerStubHelper {

    static void givenClusterAnnounceIpConfigurationFails(@NonNull RedisClusterAnnounceIpConfigurer clusterAnnounceIpConfigurer,
                                                         @NonNull GenericContainer<?> container) {
        var failure = new IllegalStateException("Docker command has failed with [-1] code");

        doThrow(failure)
                .when(clusterAnnounceIpConfigurer)
                .configure(any(), eq(container));
    }

}
