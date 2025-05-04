package pl.netroute.hussar.service.nosql.redis.assertion;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import pl.netroute.hussar.core.api.Endpoint;
import pl.netroute.hussar.core.configuration.api.ConfigurationEntry;
import pl.netroute.hussar.core.configuration.api.EnvVariableConfigurationEntry;
import pl.netroute.hussar.core.configuration.api.PropertyConfigurationEntry;
import pl.netroute.hussar.core.helper.EndpointHelper;
import pl.netroute.hussar.service.nosql.redis.api.RedisDockerService;
import pl.netroute.hussar.service.nosql.redis.api.RedisCredentials;
import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.DefaultRedisCredentials;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.time.Duration;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor
public class RedisAssertionHelper {
    private static final int SINGLE = 1;

    private static final Duration TIMEOUT = Duration.ofSeconds(5L);

    private static final String PING_RESULT = "PONG";

    @NonNull
    private final RedisDockerService redis;

    public void assertSingleEndpoint() {
        assertThat(redis.getEndpoints()).hasSize(SINGLE);
    }

    public void asserRedisAccessible() {
        var endpoint = EndpointHelper.getAnyEndpointOrFail(redis);

        try(var client = createClient(endpoint)) {
            var result = client.ping();

            assertThat(result).isEqualTo(PING_RESULT);
        }
    }

    public void assertRedisNotAccessible() {
        var endpoint = EndpointHelper.getAnyEndpointOrFail(redis);

        assertRedisNotAccessible(endpoint);
    }

    public void assertRedisNotAccessible(@NonNull Endpoint endpoint) {
        try(var client = createClient(endpoint)) {
            throw new AssertionError("Expected JedisConnectionException");
        } catch (JedisConnectionException ex) {
        } catch (Exception ex) {
            throw new AssertionError("Expected JedisConnectionException");
        }
    }

    public void assertRegisteredEndpointUnderProperty(@NonNull String registeredProperty) {
        var endpoint = EndpointHelper.getAnyEndpointOrFail(redis);

        assertRegisteredEntryInConfigRegistry(registeredProperty, endpoint.address(), PropertyConfigurationEntry.class);
    }

    public void assertRegisteredEndpointUnderEnvironmentVariable(@NonNull String registeredEnvVariable) {
        var endpoint = EndpointHelper.getAnyEndpointOrFail(redis);

        assertRegisteredEntryInConfigRegistry(registeredEnvVariable, endpoint.address(), EnvVariableConfigurationEntry.class);
    }

    public void assertRegisteredUsernameUnderProperty(@NonNull String registeredProperty) {
        var credentials = redis.getCredentials();

        assertRegisteredEntryInConfigRegistry(registeredProperty, credentials.username(), PropertyConfigurationEntry.class);
    }

    public void assertRegisteredUsernameUnderEnvironmentVariable(@NonNull String registeredEnvVariable) {
        var credentials = redis.getCredentials();

        assertRegisteredEntryInConfigRegistry(registeredEnvVariable, credentials.username(), EnvVariableConfigurationEntry.class);
    }

    public void assertRegisteredPasswordUnderProperty(@NonNull String registeredProperty) {
        var credentials = redis.getCredentials();

        assertRegisteredEntryInConfigRegistry(registeredProperty, credentials.password(), PropertyConfigurationEntry.class);
    }

    public void assertRegisteredPasswordUnderEnvironmentVariable(@NonNull String registeredEnvVariable) {
        var credentials = redis.getCredentials();

        assertRegisteredEntryInConfigRegistry(registeredEnvVariable, credentials.password(), EnvVariableConfigurationEntry.class);
    }

    public void assertNoEntriesRegistered() {
        var entriesRegistered = redis
                .getConfigurationRegistry()
                .getEntries();

        assertThat(entriesRegistered).isEmpty();
    }

    private void assertRegisteredEntryInConfigRegistry(String entryName, String entryValue, Class<? extends ConfigurationEntry> configType) {
        var configRegistry = redis.getConfigurationRegistry();

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

    private Jedis createClient(Endpoint endpoint) {
        var host = endpoint.host();
        var port = endpoint.port();
        var timeout = (int) TIMEOUT.toMillis();
        var credentials = redis.getCredentials();

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

        return new Jedis(host, port, configBuilder.build());
    }

    private redis.clients.jedis.RedisCredentials createCredentials(RedisCredentials credentials) {
        var username = credentials.username();
        var passwordChars = credentials.password().toCharArray();

        return new DefaultRedisCredentials(username, passwordChars);
    }

}
