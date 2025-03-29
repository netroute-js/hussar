package pl.netroute.hussar.junit5.assertion;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.assertj.core.api.Assertions;
import pl.netroute.hussar.core.api.Endpoint;
import pl.netroute.hussar.core.application.api.Application;
import pl.netroute.hussar.junit5.helper.ApplicationClientRunner;
import pl.netroute.hussar.service.nosql.redis.api.RedisClusterDockerService;
import pl.netroute.hussar.service.nosql.redis.api.RedisCredentials;
import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.DefaultRedisCredentials;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static pl.netroute.hussar.junit5.assertion.ApplicationPropertiesAssertionHelper.assertPropertyConfigured;
import static pl.netroute.hussar.junit5.config.ApplicationProperties.REDIS_CLUSTER_ALTERNATIVE_PASSWORD_PROPERTY;
import static pl.netroute.hussar.junit5.config.ApplicationProperties.REDIS_CLUSTER_ALTERNATIVE_URL_PROPERTY;
import static pl.netroute.hussar.junit5.config.ApplicationProperties.REDIS_CLUSTER_ALTERNATIVE_USERNAME_PROPERTY;
import static pl.netroute.hussar.junit5.config.ApplicationProperties.REDIS_CLUSTER_PASSWORD_PROPERTY;
import static pl.netroute.hussar.junit5.config.ApplicationProperties.REDIS_CLUSTER_URL_PROPERTY;
import static pl.netroute.hussar.junit5.config.ApplicationProperties.REDIS_CLUSTER_USERNAME_PROPERTY;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RedisClusterAssertionHelper {
    private static final String PING_RESULT = "PONG";
    private static final String ENDPOINTS_JOIN_DELIMITER = ",";

    private static final Duration TIMEOUT = Duration.ofSeconds(10L);

    public static void assertRedisClusterBootstrapped(@NonNull RedisClusterDockerService redisClusterService,
                                                      @NonNull Application application) {
        var endpoints = redisClusterService.getEndpoints();
        var squashedEndpoints = squashEndpoints(endpoints);
        var credentials = redisClusterService.getCredentials();
        var applicationClientRunner = new ApplicationClientRunner(application);

        assertRedisReachable(endpoints, credentials);
        applicationClientRunner.run(applicationClient -> assertPropertyConfigured(REDIS_CLUSTER_URL_PROPERTY, squashedEndpoints, applicationClient));
        applicationClientRunner.run(applicationClient -> assertPropertyConfigured(REDIS_CLUSTER_ALTERNATIVE_URL_PROPERTY, squashedEndpoints, applicationClient));
        applicationClientRunner.run(applicationClient -> assertPropertyConfigured(REDIS_CLUSTER_USERNAME_PROPERTY, credentials.username(), applicationClient));
        applicationClientRunner.run(applicationClient -> assertPropertyConfigured(REDIS_CLUSTER_ALTERNATIVE_USERNAME_PROPERTY, credentials.username(), applicationClient));
        applicationClientRunner.run(applicationClient -> assertPropertyConfigured(REDIS_CLUSTER_PASSWORD_PROPERTY, credentials.password(), applicationClient));
        applicationClientRunner.run(applicationClient -> assertPropertyConfigured(REDIS_CLUSTER_ALTERNATIVE_PASSWORD_PROPERTY, credentials.password(), applicationClient));
    }

    private static void assertRedisReachable(List<Endpoint> endpoints, RedisCredentials credentials) {
        try(var client = createClient(endpoints, credentials)) {
            Assertions.assertThat(client.ping()).isEqualTo(PING_RESULT);
        }
    }

    private static JedisCluster createClient(List<Endpoint> endpoints, RedisCredentials credentials) {
        var nodes = endpoints
                .stream()
                .map(endpoint -> new HostAndPort(endpoint.host(), endpoint.port()))
                .collect(Collectors.toUnmodifiableSet());

        var timeout = (int) TIMEOUT.toMillis();

        var configBuilder = DefaultJedisClientConfig
                .builder()
                .timeoutMillis(timeout)
                .connectionTimeoutMillis(timeout)
                .socketTimeoutMillis(timeout);

        Optional
                .of(credentials)
                .filter(RedisCredentials::isPasswordEnabled)
                .map(RedisClusterAssertionHelper::createCredentials)
                .ifPresent(configBuilder::credentials);

        return new JedisCluster(nodes, configBuilder.build());
    }

    private static redis.clients.jedis.RedisCredentials createCredentials(RedisCredentials credentials) {
        var username = credentials.username();
        var passwordChars = credentials.password().toCharArray();

        return new DefaultRedisCredentials(username, passwordChars);
    }

    private static String squashEndpoints(List<Endpoint> endpoints) {
        return endpoints
                .stream()
                .map(Endpoint::address)
                .collect(Collectors.joining(ENDPOINTS_JOIN_DELIMITER));
    }
}
