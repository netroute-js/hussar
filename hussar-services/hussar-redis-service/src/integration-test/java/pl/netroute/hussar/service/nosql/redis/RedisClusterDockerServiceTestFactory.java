package pl.netroute.hussar.service.nosql.redis;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import pl.netroute.hussar.core.service.ServiceConfigureContext;
import pl.netroute.hussar.service.nosql.redis.api.RedisClusterDockerService;
import pl.netroute.hussar.service.nosql.redis.api.RedisClusterDockerServiceConfigurer;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class RedisClusterDockerServiceTestFactory {

    public static RedisClusterDockerService createMinimallyConfigured(@NonNull ServiceConfigureContext context) {
        return RedisClusterDockerServiceConfigurer
                .newInstance()
                .done()
                .configure(context);
    }

}
