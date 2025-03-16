package pl.netroute.hussar.service.nosql.redis.api;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.testcontainers.containers.FixedHostPortGenericContainer;

import static org.mockito.Mockito.doThrow;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class RedisClusterNoProtectionConfigurerStubHelper {

    static void givenNoProtectionConfigurationFails(@NonNull RedisClusterNoProtectionConfigurer clusterNoProtectionConfigurer,
                                                    @NonNull FixedHostPortGenericContainer<?> container) {
        var failure = new IllegalStateException("Docker command has failed with [-1] code");

        doThrow(failure)
                .when(clusterNoProtectionConfigurer)
                .configure(container);
    }

}
