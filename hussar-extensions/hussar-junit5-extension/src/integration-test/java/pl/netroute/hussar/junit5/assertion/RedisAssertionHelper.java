package pl.netroute.hussar.junit5.assertion;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.assertj.core.api.Assertions;
import pl.netroute.hussar.core.api.Endpoint;
import pl.netroute.hussar.core.helper.EndpointHelper;
import pl.netroute.hussar.junit5.client.SimpleApplicationClient;
import pl.netroute.hussar.service.nosql.redis.RedisDockerService;
import pl.netroute.hussar.service.nosql.redis.api.RedisCredentials;
import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.DefaultRedisCredentials;
import redis.clients.jedis.Jedis;

import java.time.Duration;
import java.util.Optional;

import static pl.netroute.hussar.junit5.assertion.ApplicationPropertiesAssertionHelper.assertPropertyConfigured;
import static pl.netroute.hussar.junit5.config.ApplicationProperties.REDIS_ALTERNATIVE_PASSWORD_PROPERTY;
import static pl.netroute.hussar.junit5.config.ApplicationProperties.REDIS_ALTERNATIVE_URL_PROPERTY;
import static pl.netroute.hussar.junit5.config.ApplicationProperties.REDIS_ALTERNATIVE_USERNAME_PROPERTY;
import static pl.netroute.hussar.junit5.config.ApplicationProperties.REDIS_PASSWORD_PROPERTY;
import static pl.netroute.hussar.junit5.config.ApplicationProperties.REDIS_URL_PROPERTY;
import static pl.netroute.hussar.junit5.config.ApplicationProperties.REDIS_USERNAME_PROPERTY;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RedisAssertionHelper {
    private static final String PING_RESULT = "PONG";

    private static final Duration TIMEOUT = Duration.ofSeconds(10L);

    public static void assertRedisBootstrapped(@NonNull RedisDockerService redisService,
                                               @NonNull SimpleApplicationClient applicationClient) {
        var endpoint = EndpointHelper.getAnyEndpointOrFail(redisService);
        var credentials = redisService.getCredentials();

        assertRedisReachable(endpoint, credentials);
        assertPropertyConfigured(REDIS_URL_PROPERTY, endpoint.address(), applicationClient);
        assertPropertyConfigured(REDIS_ALTERNATIVE_URL_PROPERTY, endpoint.address(), applicationClient);
        assertPropertyConfigured(REDIS_USERNAME_PROPERTY, credentials.username(), applicationClient);
        assertPropertyConfigured(REDIS_ALTERNATIVE_USERNAME_PROPERTY, credentials.username(), applicationClient);
        assertPropertyConfigured(REDIS_PASSWORD_PROPERTY, credentials.password(), applicationClient);
        assertPropertyConfigured(REDIS_ALTERNATIVE_PASSWORD_PROPERTY, credentials.password(), applicationClient);
    }

    private static void assertRedisReachable(Endpoint endpoint, RedisCredentials credentials) {
        var client = createClient(endpoint, credentials);

        Assertions.assertThat(client.ping()).isEqualTo(PING_RESULT);
    }

    private static Jedis createClient(Endpoint endpoint, RedisCredentials credentials) {
        var host = endpoint.host();
        var port = endpoint.port();
        var timeout = (int) TIMEOUT.toMillis();

        var configBuilder = DefaultJedisClientConfig
                .builder()
                .timeoutMillis(timeout)
                .connectionTimeoutMillis(timeout)
                .socketTimeoutMillis(timeout);

        Optional
                .of(credentials)
                .filter(RedisCredentials::isPasswordEnabled)
                .map(RedisAssertionHelper::createCredentials)
                .ifPresent(configBuilder::credentials);

        return new Jedis(host, port, configBuilder.build());
    }

    private static redis.clients.jedis.RedisCredentials createCredentials(RedisCredentials credentials) {
        var username = credentials.username();
        var passwordChars = credentials.password().toCharArray();

        return new DefaultRedisCredentials(username, passwordChars);
    }
}
