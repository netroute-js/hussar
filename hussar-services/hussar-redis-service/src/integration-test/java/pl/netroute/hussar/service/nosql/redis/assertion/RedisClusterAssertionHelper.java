package pl.netroute.hussar.service.nosql.redis.assertion;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import pl.netroute.hussar.core.api.Endpoint;
import pl.netroute.hussar.core.configuration.api.ConfigurationEntry;
import pl.netroute.hussar.core.configuration.api.EnvVariableConfigurationEntry;
import pl.netroute.hussar.core.configuration.api.PropertyConfigurationEntry;
import pl.netroute.hussar.service.nosql.redis.api.RedisClusterDockerService;
import pl.netroute.hussar.service.nosql.redis.api.RedisCredentials;
import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.DefaultRedisCredentials;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.exceptions.JedisClusterOperationException;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor
public class RedisClusterAssertionHelper {
    private static final int ENDPOINTS = 6;

    private static final Duration TIMEOUT = Duration.ofSeconds(20L);

    private static final String PING_RESULT = "PONG";
    private static final String ENDPOINTS_JOIN_DELIMITER = ",";

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

    public void assertRedisClusterNotAccessible(@NonNull List<Endpoint> endpoints) {
        try(var client = createClient(endpoints)) {
            throw new AssertionError("Expected JedisClusterOperationException");
        } catch (JedisClusterOperationException ex) {
        } catch (Exception ex) {
            throw new AssertionError("Expected JedisClusterOperationException");
        }
    }

    public void assertRegisteredEndpointsUnderProperty(@NonNull String registeredProperty) {
        var endpoints = redisCluster.getEndpoints();
        var squashedEndpoints = squashEndpoints(endpoints);

        assertRegisteredEntryInConfigRegistry(registeredProperty, squashedEndpoints, PropertyConfigurationEntry.class);
    }

    public void assertRegisteredEndpointsUnderEnvironmentVariable(@NonNull String registeredEnvVariable) {
        var endpoints = redisCluster.getEndpoints();
        var squashedEndpoints = squashEndpoints(endpoints);

        assertRegisteredEntryInConfigRegistry(registeredEnvVariable, squashedEndpoints, EnvVariableConfigurationEntry.class);
    }

    public void assertRegisteredUsernameUnderProperty(@NonNull String registeredProperty) {
        var credentials = redisCluster.getCredentials();

        assertRegisteredEntryInConfigRegistry(registeredProperty, credentials.username(), PropertyConfigurationEntry.class);
    }

    public void assertRegisteredUsernameUnderEnvironmentVariable(@NonNull String registeredEnvVariable) {
        var credentials = redisCluster.getCredentials();

        assertRegisteredEntryInConfigRegistry(registeredEnvVariable, credentials.username(), EnvVariableConfigurationEntry.class);
    }

    public void assertRegisteredPasswordUnderProperty(@NonNull String registeredProperty) {
        var credentials = redisCluster.getCredentials();

        assertRegisteredEntryInConfigRegistry(registeredProperty, credentials.password(), PropertyConfigurationEntry.class);
    }

    public void assertRegisteredPasswordUnderEnvironmentVariable(@NonNull String registeredEnvVariable) {
        var credentials = redisCluster.getCredentials();

        assertRegisteredEntryInConfigRegistry(registeredEnvVariable, credentials.password(), EnvVariableConfigurationEntry.class);
    }

    public void assertNoEntriesRegistered() {
        var entriesRegistered = redisCluster
                .getConfigurationRegistry()
                .getEntries();

        assertThat(entriesRegistered).isEmpty();
    }

    private void assertRegisteredEntryInConfigRegistry(String entryName, String entryValue, Class<? extends ConfigurationEntry> configType) {
        var configRegistry = redisCluster.getConfigurationRegistry();

        configRegistry
                .getEntries()
                .stream()
                .filter(configEntry -> configEntry.getClass().equals(configType))
                .filter(configEntry -> configEntry.name().equals(entryName))
                .findFirst()
                .ifPresentOrElse(
                        configEntry -> assertThat(configEntry.value()).isEqualTo(entryValue),
                        () -> { throw new AssertionError("Expected registered entry in config registry. Found none"); }
                );
    }

    private JedisCluster createClient(List<Endpoint> endpoints) {
        var nodes = endpoints
                .stream()
                .map(endpoint -> new HostAndPort(endpoint.host(), endpoint.port()))
                .collect(Collectors.toUnmodifiableSet());

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

    private String squashEndpoints(List<Endpoint> endpoints) {
        return endpoints
                .stream()
                .map(Endpoint::address)
                .collect(Collectors.joining(ENDPOINTS_JOIN_DELIMITER));
    }

}
