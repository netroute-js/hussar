package pl.netroute.hussar.service.nosql.redis.api;

import lombok.NonNull;
import pl.netroute.hussar.core.helper.StringHelper;

public record RedisCredentials(@NonNull String username,
                               String password) {
    private static final String REDIS_NO_PASSWORD = null;

    public boolean isPasswordEnabled() {
        return StringHelper.hasValue(password);
    }

    public static RedisCredentials passwordLess(@NonNull String username) {
        return new RedisCredentials(username, REDIS_NO_PASSWORD);
    }

}
