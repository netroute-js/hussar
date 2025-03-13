package pl.netroute.hussar.service.nosql.redis.api;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.testcontainers.containers.FixedHostPortGenericContainer;

import static org.mockito.Mockito.verify;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class RedisClusterNoProtectionConfigurerAssertionHelper {

    static void assertClusterNoProtectionConfigured(@NonNull RedisClusterNoProtectionConfigurer clusterNoProtectionConfigurer,
                                                    @NonNull FixedHostPortGenericContainer<?> container) {
        verify(clusterNoProtectionConfigurer).configure(container);
    }

}
