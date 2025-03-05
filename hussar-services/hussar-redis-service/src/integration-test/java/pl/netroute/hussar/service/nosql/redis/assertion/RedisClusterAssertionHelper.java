package pl.netroute.hussar.service.nosql.redis.assertion;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import pl.netroute.hussar.core.api.Endpoint;
import pl.netroute.hussar.service.nosql.redis.api.RedisClusterDockerService;
import pl.netroute.hussar.service.nosql.redis.api.RedisCredentials;
import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.DefaultRedisCredentials;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor
public class RedisClusterAssertionHelper {
    private static final int ENDPOINTS = 6;

    private static final Duration TIMEOUT = Duration.ofSeconds(20L);

    private static final String PING_RESULT = "PONG";

    @NonNull
    private final RedisClusterDockerService redisCluster;

    public void assertMultipleEndpoints() {
        assertThat(redisCluster.getEndpoints()).hasSize(ENDPOINTS);
    }

    public void asserRedisClusterAccessible() {
        var endpoints = redisCluster.getEndpoints();

        try(var client = createClient(endpoints)) {
            var result = client.ping();

            assertThat(result).isEqualTo(PING_RESULT);
        }
    }

    private JedisCluster createClient(List<Endpoint> endpoints) {
        // remove redis:// ???
//        var nodes = endpoints
//                .stream()
//                .map(endpoint -> new HostAndPort(endpoint.host(), endpoint.port()))
//                .collect(Collectors.toUnmodifiableSet());

        var nodes = Set.of(
                new HostAndPort("localhost", 7000)
        );

        var timeout = (int) TIMEOUT.toMillis();
        var credentials = redisCluster.getCredentials();

        var configBuilder = DefaultJedisClientConfig
                .builder()
                .timeoutMillis(timeout)
                .connectionTimeoutMillis(timeout)
                .socketTimeoutMillis(timeout);

        Optional
                .of(credentials)
                .filter(RedisCredentials::isPasswordEnabled)
                .map(this::createCredentials)
                .ifPresent(configBuilder::credentials);

        return new JedisCluster(nodes, configBuilder.build());
    }

    private redis.clients.jedis.RedisCredentials createCredentials(RedisCredentials credentials) {
        var username = credentials.username();
        var passwordChars = credentials.password().toCharArray();

        return new DefaultRedisCredentials(username, passwordChars);
    }
}
