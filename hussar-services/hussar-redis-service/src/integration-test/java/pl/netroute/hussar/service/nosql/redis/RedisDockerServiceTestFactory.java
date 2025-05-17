package pl.netroute.hussar.service.nosql.redis;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import pl.netroute.hussar.core.service.ServiceConfigureContext;
import pl.netroute.hussar.service.nosql.redis.api.RedisDockerService;
import pl.netroute.hussar.service.nosql.redis.api.RedisDockerServiceConfigurer;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class RedisDockerServiceTestFactory {

    public static RedisDockerService createMinimallyConfigured(@NonNull ServiceConfigureContext context) {
        return RedisDockerServiceConfigurer
                .newInstance()
                .done()
                .configure(context);
    }

}
