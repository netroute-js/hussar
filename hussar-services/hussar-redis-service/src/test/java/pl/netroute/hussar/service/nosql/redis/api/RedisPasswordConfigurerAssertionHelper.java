package pl.netroute.hussar.service.nosql.redis.api;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.testcontainers.containers.GenericContainer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class RedisPasswordConfigurerAssertionHelper {

    static void assertPasswordConfigured(@NonNull RedisPasswordConfigurer passwordConfigurer,
                                         @NonNull RedisCredentials credentials,
                                         @NonNull GenericContainer<?> container) {
        verify(passwordConfigurer).configure(credentials, container);
    }

    static void assertNoPasswordConfigured(@NonNull RedisPasswordConfigurer passwordConfigurer) {
        verify(passwordConfigurer, never()).configure(any(), any());
    }

}
