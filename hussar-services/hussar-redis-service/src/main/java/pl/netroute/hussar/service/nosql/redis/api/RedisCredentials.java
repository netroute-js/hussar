package pl.netroute.hussar.service.nosql.redis.api;

import lombok.NonNull;
import pl.netroute.hussar.core.helper.StringHelper;

/**
 * A custom type representing Redis credentials.
 */
public record RedisCredentials(@NonNull String username,
                               String password) {
    private static final String REDIS_NO_PASSWORD = null;

    /**
     * Returns whether the password is present.
     *
     * @return true if it's enabled. False otherwise.
     */
    public boolean isPasswordEnabled() {
        return StringHelper.hasValue(password);
    }

    /**
     * Factory method to create password less {@link RedisCredentials}.
     *
     * @param username - the username
     * @return an instance of {@link RedisCredentials}
     */
    public static RedisCredentials passwordLess(@NonNull String username) {
        return new RedisCredentials(username, REDIS_NO_PASSWORD);
    }

}
