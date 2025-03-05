package pl.netroute.hussar.service.nosql.redis.api;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class RedisSettings {
    static final int REDIS_LISTENING_PORT = 6379;

    static final String REDIS_USERNAME = "default";
    static final String REDIS_PASSWORD = "test";
}
